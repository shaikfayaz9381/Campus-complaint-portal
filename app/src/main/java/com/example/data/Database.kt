package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val rollNumber: String,
    val department: String,
    val year: String,
    val phone: String,
    val email: String,
    val password: String
)

@Entity(tableName = "complaints")
data class Complaint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Int,
    val title: String,
    val category: String,
    val description: String,
    val priority: String, // Low, Medium, High
    val location: String,
    val imagePath: String? = null,
    val status: String = "Pending", // Pending, Under Review, In Progress, Resolved, Rejected
    val remarks: String = "",
    val createdDate: Long = System.currentTimeMillis(),
    val updatedDate: Long = System.currentTimeMillis()
)

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentByIdFlow(id: Int): Flow<Student?>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Int): Student?

    @Query("SELECT * FROM students WHERE email = :email LIMIT 1")
    suspend fun getStudentByEmail(email: String): Student?

    @Query("SELECT * FROM students WHERE rollNumber = :rollNumber LIMIT 1")
    suspend fun getStudentByRollNumber(rollNumber: String): Student?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: Int)

    @Delete
    suspend fun deleteStudent(student: Student)
}

@Dao
interface ComplaintDao {
    @Query("SELECT * FROM complaints ORDER BY createdDate DESC")
    fun getAllComplaints(): Flow<List<Complaint>>

    @Query("SELECT * FROM complaints WHERE studentId = :studentId ORDER BY createdDate DESC")
    fun getComplaintsByStudent(studentId: Int): Flow<List<Complaint>>

    @Query("SELECT * FROM complaints WHERE id = :id")
    fun getComplaintByIdFlow(id: Long): Flow<Complaint?>

    @Query("SELECT * FROM complaints WHERE id = :id")
    suspend fun getComplaintById(id: Long): Complaint?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: Complaint): Long

    @Update
    suspend fun updateComplaint(complaint: Complaint)

    @Query("DELETE FROM complaints WHERE id = :id")
    suspend fun deleteComplaintById(id: Long)

    @Delete
    suspend fun deleteComplaint(complaint: Complaint)
}

@Database(entities = [Student::class, Complaint::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun complaintDao(): ComplaintDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ccms_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
