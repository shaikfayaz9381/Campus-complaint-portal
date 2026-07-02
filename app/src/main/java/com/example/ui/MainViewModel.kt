package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Complaint
import com.example.data.ComplaintRepository
import com.example.data.Student
import com.example.util.BackupRestoreHelper
import com.example.util.NotificationHelper
import com.example.util.PdfExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

sealed interface Screen {
    object Splash : Screen
    object Login : Screen
    object Register : Screen
    object StudentDashboard : Screen
    object SubmitComplaint : Screen
    object ComplaintHistory : Screen
    data class ComplaintDetails(val complaintId: Long) : Screen
    object StudentProfile : Screen
    object AdminDashboard : Screen
    object AdminComplaintManagement : Screen
    object AdminStudentManagement : Screen
    object AdminSettings : Screen
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = ComplaintRepository(db.studentDao(), db.complaintDao())

    private val sharedPrefs = application.getSharedPreferences("ccms_prefs", Context.MODE_PRIVATE)

    // Navigation and Auth State
    val currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val navigationStack = mutableListOf<Screen>()

    val loggedInStudent = MutableStateFlow<Student?>(null)
    val isAdminLoggedIn = MutableStateFlow(false)

    // Search, Filter and Sort States for Student History
    val studentSearchQuery = MutableStateFlow("")
    val studentCategoryFilter = MutableStateFlow("All")
    val studentStatusFilter = MutableStateFlow("All")
    val studentSortNewest = MutableStateFlow(true) // true = newest, false = oldest

    // Search and Filter States for Admin Complaints
    val adminSearchQuery = MutableStateFlow("") // matches title, student name, roll number
    val adminCategoryFilter = MutableStateFlow("All")
    val adminStatusFilter = MutableStateFlow("All")

    // Search and Filter States for Admin Student Management
    val adminStudentSearchQuery = MutableStateFlow("")
    val selectedStudentForHistory = MutableStateFlow<Student?>(null)

    // Flow lists
    val allStudents: StateFlow<List<Student>> = repository.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allComplaints: StateFlow<List<Complaint>> = repository.getAllComplaints()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Complaints for Admin
    val filteredAdminComplaints: StateFlow<List<Complaint>> = combine(
        allComplaints,
        allStudents,
        adminSearchQuery,
        adminCategoryFilter,
        adminStatusFilter
    ) { complaints, students, search, category, status ->
        complaints.filter { complaint ->
            val student = students.find { it.id == complaint.studentId }
            val matchesSearch = search.isEmpty() ||
                    complaint.title.contains(search, ignoreCase = true) ||
                    complaint.description.contains(search, ignoreCase = true) ||
                    (student?.name?.contains(search, ignoreCase = true) == true) ||
                    (student?.rollNumber?.contains(search, ignoreCase = true) == true)

            val matchesCategory = category == "All" || complaint.category == category
            val matchesStatus = status == "All" || complaint.status == status

            matchesSearch && matchesCategory && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Registered Students for Admin
    val filteredAdminStudents: StateFlow<List<Student>> = combine(
        allStudents,
        adminStudentSearchQuery
    ) { students, search ->
        students.filter { student ->
            search.isEmpty() ||
                    student.name.contains(search, ignoreCase = true) ||
                    student.rollNumber.contains(search, ignoreCase = true) ||
                    student.department.contains(search, ignoreCase = true) ||
                    student.email.contains(search, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Complaints for Currently Logged In Student
    val filteredStudentComplaints: StateFlow<List<Complaint>> = combine(
        loggedInStudent,
        studentSearchQuery,
        studentCategoryFilter,
        studentStatusFilter,
        studentSortNewest
    ) { student, search, category, status, sortNewest ->
        if (student == null) {
            emptyList()
        } else {
            val list = repository.getComplaintsForStudent(student.id).first()
            list.filter { complaint ->
                val matchesSearch = search.isEmpty() ||
                        complaint.title.contains(search, ignoreCase = true) ||
                        complaint.description.contains(search, ignoreCase = true)
                val matchesCategory = category == "All" || complaint.category == category
                val matchesStatus = status == "All" || complaint.status == status
                matchesSearch && matchesCategory && matchesStatus
            }.sortedWith { c1, c2 ->
                if (sortNewest) c2.createdDate.compareTo(c1.createdDate)
                else c1.createdDate.compareTo(c2.createdDate)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        checkSavedSession()
    }

    private fun checkSavedSession() {
        val studentId = sharedPrefs.getInt("logged_in_student_id", -1)
        val isAdmin = sharedPrefs.getBoolean("is_admin_logged_in", false)

        if (isAdmin) {
            isAdminLoggedIn.value = true
            navigateTo(Screen.AdminDashboard)
        } else if (studentId != -1) {
            viewModelScope.launch {
                val student = repository.getStudentByIdDirect(studentId)
                if (student != null) {
                    loggedInStudent.value = student
                    navigateTo(Screen.StudentDashboard)
                } else {
                    navigateTo(Screen.Login)
                }
            }
        } else {
            // Splash timer transition
            viewModelScope.launch {
                kotlinx.coroutines.delay(1500)
                navigateTo(Screen.Login)
            }
        }
    }

    // Navigation helpers with custom backstack
    fun navigateTo(screen: Screen, clearStack: Boolean = false) {
        if (clearStack) {
            navigationStack.clear()
        } else {
            val current = currentScreen.value
            if (current != Screen.Splash) {
                navigationStack.add(current)
            }
        }
        currentScreen.value = screen
    }

    fun navigateBack() {
        if (navigationStack.isNotEmpty()) {
            val previous = navigationStack.removeAt(navigationStack.size - 1)
            currentScreen.value = previous
        }
    }

    // Auth Operations
    fun registerStudent(student: Student, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.registerStudent(student)
            if (result.isSuccess) {
                onResult(true, "Registration Successful! Please Login.")
                navigateTo(Screen.Login)
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Registration Failed")
            }
        }
    }

    fun loginUser(emailOrUsername: String, passwordOrPin: String, onResult: (Boolean, String) -> Unit) {
        if (emailOrUsername == "admin" && passwordOrPin == "admin123") {
            isAdminLoggedIn.value = true
            sharedPrefs.edit()
                .putBoolean("is_admin_logged_in", true)
                .putInt("logged_in_student_id", -1)
                .apply()
            onResult(true, "Admin logged in successfully!")
            navigateTo(Screen.AdminDashboard, clearStack = true)
            return
        }

        viewModelScope.launch {
            val result = repository.loginStudent(emailOrUsername, passwordOrPin)
            if (result.isSuccess) {
                val student = result.getOrNull()!!
                loggedInStudent.value = student
                sharedPrefs.edit()
                    .putInt("logged_in_student_id", student.id)
                    .putBoolean("is_admin_logged_in", false)
                    .apply()
                onResult(true, "Welcome back, ${student.name}!")
                navigateTo(Screen.StudentDashboard, clearStack = true)
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Login Failed")
            }
        }
    }

    fun logout() {
        loggedInStudent.value = null
        isAdminLoggedIn.value = false
        sharedPrefs.edit()
            .remove("logged_in_student_id")
            .remove("is_admin_logged_in")
            .apply()
        navigateTo(Screen.Login, clearStack = true)
    }

    fun updateStudentProfile(updatedStudent: Student, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateStudent(updatedStudent)
                loggedInStudent.value = updatedStudent
                onResult(true, "Profile updated successfully!")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to update profile")
            }
        }
    }

    // Complaint Operations
    fun submitComplaint(
        title: String,
        category: String,
        description: String,
        priority: String,
        location: String,
        imageUri: Uri?,
        onResult: (Boolean, String) -> Unit
    ) {
        val student = loggedInStudent.value
        if (student == null) {
            onResult(false, "Authentication Error")
            return
        }

        viewModelScope.launch {
            try {
                var localImagePath: String? = null
                if (imageUri != null) {
                    localImagePath = saveImageToInternalStorage(imageUri)
                }

                val complaint = Complaint(
                    studentId = student.id,
                    title = title,
                    category = category,
                    description = description,
                    priority = priority,
                    location = location,
                    imagePath = localImagePath
                )

                repository.submitComplaint(complaint)
                NotificationHelper.showNotification(
                    getApplication(),
                    "Complaint Submitted",
                    "Your complaint '$title' has been submitted successfully."
                )
                onResult(true, "Complaint submitted successfully!")
                navigateTo(Screen.StudentDashboard)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to submit complaint")
            }
        }
    }

    fun editPendingComplaint(
        complaintId: Long,
        title: String,
        category: String,
        description: String,
        priority: String,
        location: String,
        imageUri: Uri?,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val existing = repository.getComplaintByIdDirect(complaintId)
                if (existing == null || existing.status != "Pending") {
                    onResult(false, "Only Pending complaints can be edited.")
                    return@launch
                }

                var localImagePath = existing.imagePath
                if (imageUri != null) {
                    localImagePath = saveImageToInternalStorage(imageUri)
                }

                val updated = existing.copy(
                    title = title,
                    category = category,
                    description = description,
                    priority = priority,
                    location = location,
                    imagePath = localImagePath,
                    updatedDate = System.currentTimeMillis()
                )

                repository.updateComplaint(updated)
                onResult(true, "Complaint updated successfully!")
                navigateBack()
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to update complaint")
            }
        }
    }

    fun deletePendingComplaint(complaintId: Long, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val existing = repository.getComplaintByIdDirect(complaintId)
                if (existing == null) {
                    onResult(false, "Complaint not found")
                    return@launch
                }
                if (existing.status != "Pending") {
                    onResult(false, "Only Pending complaints can be deleted.")
                    return@launch
                }
                repository.deleteComplaintById(complaintId)
                onResult(true, "Complaint deleted successfully!")
                navigateBack()
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to delete complaint")
            }
        }
    }

    // Admin Complaint Management Operations
    fun updateComplaintStatusAndRemarks(
        complaintId: Long,
        status: String,
        remarks: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val existing = repository.getComplaintByIdDirect(complaintId)
                if (existing == null) {
                    onResult(false, "Complaint not found")
                    return@launch
                }

                val updated = existing.copy(
                    status = status,
                    remarks = remarks,
                    updatedDate = System.currentTimeMillis()
                )

                repository.updateComplaint(updated)

                // Notify student
                val studentName = repository.getStudentByIdDirect(existing.studentId)?.name ?: "Student"
                NotificationHelper.showNotification(
                    getApplication(),
                    "Complaint Status Updated",
                    "Dear $studentName, your complaint COMP-${existing.id} status is now '$status'."
                )

                onResult(true, "Complaint updated successfully!")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to update complaint")
            }
        }
    }

    fun adminDeleteComplaint(complaintId: Long, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteComplaintById(complaintId)
                onResult(true, "Complaint deleted by Administrator.")
                navigateBack()
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to delete complaint")
            }
        }
    }

    // Admin Student Management Operations
    fun adminDeleteStudent(studentId: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                // Delete all student's complaints first to ensure referential integrity (clean up local files if any)
                val complaints = repository.getComplaintsForStudent(studentId).first()
                complaints.forEach {
                    repository.deleteComplaintById(it.id)
                }
                repository.deleteStudentById(studentId)
                onResult(true, "Student and their complaints deleted successfully.")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to delete student")
            }
        }
    }

    // Helpers
    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val contentResolver = getApplication<Application>().contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val dir = File(getApplication<Application>().filesDir, "images")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "img_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // PDF Export Action
    fun exportMyComplaints() {
        val student = loggedInStudent.value ?: return
        viewModelScope.launch {
            val complaints = repository.getComplaintsForStudent(student.id).first()
            val file = PdfExporter.exportComplaintsToPdf(getApplication(), complaints, student.name)
            if (file != null) {
                PdfExporter.viewPdf(getApplication(), file)
                Toast.makeText(getApplication(), "PDF Report generated at: ${file.name}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(getApplication(), "Failed to generate PDF Report", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Backup & Restore Actions
    fun backupDatabase() {
        viewModelScope.launch {
            val students = allStudents.value
            val complaints = allComplaints.value
            val json = BackupRestoreHelper.createBackup(getApplication(), students, complaints)
            if (json != null) {
                Toast.makeText(getApplication(), "Backup saved to internal storage cache successfully!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(getApplication(), "Failed to back up database", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun restoreDatabase(jsonString: String) {
        viewModelScope.launch {
            val payload = BackupRestoreHelper.parseBackup(jsonString)
            if (payload != null) {
                try {
                    payload.students.forEach { student ->
                        repository.registerStudent(student)
                    }
                    payload.complaints.forEach { complaint ->
                        repository.submitComplaint(complaint)
                    }
                    Toast.makeText(getApplication(), "Database successfully restored!", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(getApplication(), "Error during restore, some records may already exist: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(getApplication(), "Invalid or corrupted backup JSON data!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
