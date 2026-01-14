package com.fakhrinurrohman.myunjapp.data

import androidx.room.RoomDatabase

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
