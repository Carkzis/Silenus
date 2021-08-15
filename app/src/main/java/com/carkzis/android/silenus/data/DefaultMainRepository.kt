package com.carkzis.android.silenus.data

class DefaultMainRepository (
    private val mainLocalDataSource: MainDataSource,
    private val mainRemoteDataSource: MainDataSource) : MainRepository {

}