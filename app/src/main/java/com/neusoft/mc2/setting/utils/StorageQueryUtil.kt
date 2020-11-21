package com.neusoft.mc2.setting.utils

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*


object StorageQueryUtil {

    private val TAG = "StorageQueryUtil"

    fun queryWithStorageManager(context: Context) {
        val storageManager =
            context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val unit = 1024f
        val unit2 = 1000f
        val version = Build.VERSION.SDK_INT
        try {
            val getVolumes: Method = StorageManager::class.java.getDeclaredMethod("getVolumes") //6.0
            val getVolumeInfo = getVolumes.invoke(storageManager) as List<Any>
            var total = 0L
            var used = 0L
            var systemSize = 0L
            for (obj in getVolumeInfo) {
                val getType: Field = obj.javaClass.getField("type")
                val type: Int = getType.getInt(obj)
                Log.d(TAG, "type: $type")
                if (type == 1) { //TYPE_PRIVATE
                    var totalSize = 0L
                    //获取内置内存总大小
                    if (version >= Build.VERSION_CODES.O) { //8.0
                        val getFsUuid: Method = obj.javaClass.getDeclaredMethod("getFsUuid")
                        val fsUuid = getFsUuid.invoke(obj) as String
                        totalSize = getTotalSize(context, fsUuid) //8.0 以后使用
                    }
                    val isMountedReadable: Method =
                        obj.javaClass.getDeclaredMethod("isMountedReadable")
                    val readable =
                        isMountedReadable.invoke(obj) as Boolean
                    if (readable) {
                        val file: Method = obj.javaClass.getDeclaredMethod("getPath")
                        val f: File = file.invoke(obj) as File
                        if (totalSize == 0L) {
                            totalSize = f.totalSpace
                        }
                        systemSize = totalSize - f.totalSpace
                        used += totalSize - f.freeSpace
                        total += totalSize
                    }
                    Log.d(
                        TAG,
                        "type = " + type + "totalSize = " + getUnit(totalSize.toFloat(), unit)
                                + " ,used(with system) = " + getUnit(used.toFloat(), unit)
                                + " ,free = " + getUnit(totalSize - used.toFloat(), unit)
                    )
                } else if (type == 0) { //TYPE_PUBLIC
                    //外置存储
                    val isMountedReadable: Method =
                        obj.javaClass.getDeclaredMethod("isMountedReadable")
                    val readable =
                        isMountedReadable.invoke(obj) as Boolean
                    if (readable) {
                        val file: Method = obj.javaClass.getDeclaredMethod("getPath")
                        val f: File = file.invoke(obj) as File
                        used += f.getTotalSpace() - f.getFreeSpace()
                        total += f.getTotalSpace()
                    }
                } else if (type == 2) { //TYPE_EMULATED
                }
            }
            Log.d(
                TAG,
                "总内存 total = " + getUnit(
                    total.toFloat(),
                    unit
                ) + "\n已用 used(with system) = " + getUnit(used.toFloat(), unit)
                        + "可用 available = " + getUnit(
                    total - used.toFloat(),
                    unit
                ) + "系统大小：" + getUnit(systemSize.toFloat(), unit)
            )
            Log.d(
                TAG,
                "总内存 total = " + getUnit(
                    total.toFloat(),
                    unit2
                ) + "\n已用 used(with system) = " + getUnit(used.toFloat(), 1000f)
                        + "可用 available = " + getUnit(
                    total - used.toFloat(),
                    unit2
                ) + "系统大小：" + getUnit(systemSize.toFloat(), unit2)
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "缺少权限：permission.PACKAGE_USAGE_STATS")
        } catch (e: Exception) {
            e.printStackTrace()
            queryWithStatFs()
        }
    }

    fun queryWithStatFs() {
        val statFs = StatFs(Environment.getExternalStorageDirectory().getPath())

        //存储块
        val blockCount = statFs.blockCount.toLong()
        //块大小
        val blockSize = statFs.blockSize.toLong()
        //可用块数量
        val availableCount = statFs.availableBlocks.toLong()
        //剩余块数量，注：这个包含保留块（including reserved blocks）即应用无法使用的空间
        val freeBlocks = statFs.freeBlocks.toLong()

        //level 18
//        long totalSize = statFs.getTotalBytes();
//        long availableSize = statFs.getAvailableBytes();
        Log.d(TAG, "=========")
        Log.d(TAG, "total = " + getUnit(blockSize * blockCount.toFloat(), 1024f))
        Log.d(TAG, "available = " + getUnit(blockSize * availableCount.toFloat(), 1024f))
        Log.d(TAG, "free = " + getUnit(blockSize * freeBlocks.toFloat(), 1024f))
    }

    private val units =
        arrayOf("B", "KB", "MB", "GB", "TB")

    /**
     * 进制转换
     */
    fun getUnit(size: Float, base: Float): String {
        var size = size
        var index = 0
        while (size > base && index < 4) {
            size = size / base
            index++
        }
        return java.lang.String.format(Locale.getDefault(), " %.2f %s ", size, units[index])
    }

    /**
     * API 26 android O
     * 获取总共容量大小，包括系统大小
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTotalSize(context: Context, fsUuid: String?): Long {
        return try {
            val id: UUID
            id = if (fsUuid == null) {
                StorageManager.UUID_DEFAULT
            } else {
                UUID.fromString(fsUuid)
            }
            val stats: StorageStatsManager =
                context.getSystemService(StorageStatsManager::class.java)
            stats.getTotalBytes(id)
        } catch (e: NoSuchFieldError) {
            e.printStackTrace()
            -1
        } catch (e: NoClassDefFoundError) {
            e.printStackTrace()
            -1
        } catch (e: NullPointerException) {
            e.printStackTrace()
            -1
        } catch (e: IOException) {
            e.printStackTrace()
            -1
        }
    }
}