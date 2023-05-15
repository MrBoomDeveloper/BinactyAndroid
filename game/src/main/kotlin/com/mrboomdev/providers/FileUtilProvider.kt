package com.mrboomdev.providers

import com.mrboomdev.util.FileUtil

interface FileUtilProvider {
    fun remove(file: FileUtil);
    fun rename(file: FileUtil);
    fun read(file: FileUtil): String;
}