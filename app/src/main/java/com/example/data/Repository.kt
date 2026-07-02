package com.example.data

import kotlinx.coroutines.flow.Flow

class ComplaintRepository(
    private val studentDao: StudentDao,
    private val complaintDao: ComplaintDao
) {
    // Student operations
    fun getAllStudents(): Flow<List<Student>> = studentDao.getAllStudents()

    fun getStudentById(id: Int): Flow<Student?> = studentDao.getStudentByIdFlow(id)

    suspend fun getStudentByIdDirect(id: Int): Student? = studentDao.getStudentById(id)

    suspend fun registerStudent(student: Student): Result<Long> {
        val existingEmail = studentDao.getStudentByEmail(student.email)
        if (existingEmail != null) {
            return Result.failure(Exception("Email is already registered!"))
        }
        val existingRoll = studentDao.getStudentByRollNumber(student.rollNumber)
        if (existingRoll != null) {
            return Result.failure(Exception("Roll number is already registered!"))
        }
        return try {
            val id = studentDao.insertStudent(student)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginStudent(email: String, password: String): Result<Student> {
        val student = studentDao.getStudentByEmail(email)
            ?: return Result.failure(Exception("Email not found!"))
        if (student.password != password) {
            return Result.failure(Exception("Incorrect password!"))
        }
        return Result.success(student)
    }

    suspend fun updateStudent(student: Student) {
        studentDao.updateStudent(student)
    }

    suspend fun deleteStudentById(id: Int) {
        studentDao.deleteStudentById(id)
    }

    // Complaint operations
    fun getAllComplaints(): Flow<List<Complaint>> = complaintDao.getAllComplaints()

    fun getComplaintsForStudent(studentId: Int): Flow<List<Complaint>> =
        complaintDao.getComplaintsByStudent(studentId)

    fun getComplaintById(id: Long): Flow<Complaint?> = complaintDao.getComplaintByIdFlow(id)

    suspend fun getComplaintByIdDirect(id: Long): Complaint? = complaintDao.getComplaintById(id)

    suspend fun submitComplaint(complaint: Complaint): Long {
        return complaintDao.insertComplaint(complaint)
    }

    suspend fun updateComplaint(complaint: Complaint) {
        complaintDao.updateComplaint(complaint)
    }

    suspend fun deleteComplaintById(id: Long) {
        complaintDao.deleteComplaintById(id)
    }
}
