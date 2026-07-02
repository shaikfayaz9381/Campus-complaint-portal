package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Complaint
import com.example.data.Student
import com.example.ui.MainViewModel
import com.example.ui.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(viewModel: MainViewModel) {
    val students by viewModel.allStudents.collectAsState()
    val complaints by viewModel.allComplaints.collectAsState()

    val totalStudents = students.size
    val totalComplaints = complaints.size

    val pending = complaints.count { it.status == "Pending" }
    val underReview = complaints.count { it.status == "Under Review" }
    val inProgress = complaints.count { it.status == "In Progress" }
    val resolved = complaints.count { it.status == "Resolved" }
    val rejected = complaints.count { it.status == "Rejected" }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header block (Gradient M3 App Bar style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF6750A4), Color(0xFF9278D1))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 28.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "App Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Text(
                                text = "Admin Console",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Admin initials badge
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFEADDFF), CircleShape)
                                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "AD",
                                    color = Color(0xFF21005D),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                    .testTag("admin_logout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Logout",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "WELCOME BACK,",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Administrator Dashboard",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "College Complaint Management System",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Stats grid
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Operational Metrics",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatMetricCard(label = "Students", count = totalStudents, bgColor = Color(0xFFEADDFF), textColor = Color(0xFF21005D), modifier = Modifier.weight(1f))
                StatMetricCard(label = "Complaints", count = totalComplaints, bgColor = Color(0xFFEADDFF), textColor = Color(0xFF21005D), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatMetricCard(label = "Pending", count = pending, bgColor = Color(0xFFF2B8B5), textColor = Color(0xFF601410), modifier = Modifier.weight(1f))
                StatMetricCard(label = "Review", count = underReview, bgColor = Color(0xFFD1E1FF), textColor = Color(0xFF001D35), modifier = Modifier.weight(1f))
                StatMetricCard(label = "Active", count = inProgress, bgColor = Color(0xFFE8DEF8), textColor = Color(0xFF1D192B), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatMetricCard(label = "Resolved", count = resolved, bgColor = Color(0xFFC2E7CB), textColor = Color(0xFF072711), modifier = Modifier.weight(1f))
                StatMetricCard(label = "Rejected", count = rejected, bgColor = Color(0xFFF2B8B5), textColor = Color(0xFF601410), modifier = Modifier.weight(1f))
                Box(modifier = Modifier.weight(1f))
            }

            // Visual Charts Section
            if (totalComplaints > 0) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Status Distribution Chart",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20),
                    modifier = Modifier.padding(start = 20.dp, bottom = 12.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE6E0E9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        StatusPieChart(
                            pending = pending,
                            underReview = underReview,
                            inProgress = inProgress,
                            resolved = resolved,
                            rejected = rejected,
                            modifier = Modifier.size(130.dp)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        // Chart Legend
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ChartLegendItem(label = "Pending", color = Color(0xFFF2B8B5), count = pending, total = totalComplaints)
                            ChartLegendItem(label = "Under Rev.", color = Color(0xFFD1E1FF), count = underReview, total = totalComplaints)
                            ChartLegendItem(label = "In Progress", color = Color(0xFFE8DEF8), count = inProgress, total = totalComplaints)
                            ChartLegendItem(label = "Resolved", color = Color(0xFFC2E7CB), count = resolved, total = totalComplaints)
                            ChartLegendItem(label = "Rejected", color = Color(0xFFF2B8B5).copy(alpha = 0.7f), count = rejected, total = totalComplaints)
                        }
                    }
                }

                // Category Bar Chart
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Top Complaint Categories",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20),
                    modifier = Modifier.padding(start = 20.dp, bottom = 12.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE6E0E9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val categoryCounts = complaints.groupBy { it.category }.mapValues { it.value.size }
                        val topCategories = categoryCounts.entries.sortedByDescending { it.value }.take(5)

                        CategoryBarChart(topCategories = topCategories, total = totalComplaints)
                    }
                }
            }

            // Quick Navigation Panel
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Control Panels",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(start = 20.dp, bottom = 12.dp)
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminNavRow(
                    title = "Complaint Management System",
                    subtitle = "Review, update status, and remark complaints",
                    icon = Icons.Default.Assessment,
                    onClick = { viewModel.navigateTo(Screen.AdminComplaintManagement) }
                )
                AdminNavRow(
                    title = "Registered Students List",
                    subtitle = "View list of students, histories, and records",
                    icon = Icons.Default.Group,
                    onClick = { viewModel.navigateTo(Screen.AdminStudentManagement) }
                )
                AdminNavRow(
                    title = "Backup & Restore Utilities",
                    subtitle = "Export JSON backups, restore local database",
                    icon = Icons.Default.Settings,
                    onClick = { viewModel.navigateTo(Screen.AdminSettings) }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun StatusPieChart(
    pending: Int,
    underReview: Int,
    inProgress: Int,
    resolved: Int,
    rejected: Int,
    modifier: Modifier = Modifier
) {
    val total = (pending + underReview + inProgress + resolved + rejected).toFloat()
    if (total == 0f) return

    val angles = listOf(
        (pending / total) * 360f,
        (underReview / total) * 360f,
        (inProgress / total) * 360f,
        (resolved / total) * 360f,
        (rejected / total) * 360f
    )

    val colors = listOf(
        Color(0xFFF2B8B5), // Pending
        Color(0xFFD1E1FF), // Under Review
        Color(0xFFE8DEF8), // In Progress
        Color(0xFFC2E7CB), // Resolved
        Color(0xFFF2B8B5).copy(alpha = 0.7f) // Rejected
    )

    Canvas(modifier = modifier) {
        var startAngle = -90f
        angles.forEachIndexed { index, angle ->
            if (angle > 0f) {
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = angle,
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx())
                )
                startAngle += angle
            }
        }
    }
}

@Composable
fun ChartLegendItem(label: String, color: Color, count: Int, total: Int) {
    val percent = if (total > 0) (count * 100) / total else 0
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Text(
            text = "$label: $count ($percent%)",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF49454F)
        )
    }
}

@Composable
fun CategoryBarChart(topCategories: List<Map.Entry<String, Int>>, total: Int) {
    if (topCategories.isEmpty()) return

    val maxCount = topCategories.maxOf { it.value }.toFloat()

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        topCategories.forEach { entry ->
            val fraction = if (maxCount > 0) entry.value / maxCount else 0f
            val percent = if (total > 0) (entry.value * 100) / total else 0

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(entry.key, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                    Text("${entry.value} ($percent%)", fontSize = 11.sp, color = Color(0xFF49454F))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFFE6E0E9), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(8.dp)
                            .background(Color(0xFF6750A4), RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun AdminNavRow(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE6E0E9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFEADDFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = title, tint = Color(0xFF21005D))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                    Text(subtitle, fontSize = 11.sp, color = Color(0xFF49454F))
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = Color(0xFF49454F))
        }
    }
}

@Composable
fun AdminComplaintManagementScreen(viewModel: MainViewModel, onShowToast: (String) -> Unit) {
    val search by viewModel.adminSearchQuery.collectAsState()
    val categoryFilter by viewModel.adminCategoryFilter.collectAsState()
    val statusFilter by viewModel.adminStatusFilter.collectAsState()

    val filteredComplaints by viewModel.filteredAdminComplaints.collectAsState()
    val students by viewModel.allStudents.collectAsState()

    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "All", "Academic", "Hostel", "Library", "Canteen", "Transport", "Laboratory",
        "Wi-Fi", "Electrical", "Water Supply", "Sports", "Cleanliness", "Security", "Others"
    )

    val statuses = listOf("All", "Pending", "Under Review", "In Progress", "Resolved", "Rejected")

    // Active selected complaint for status editing dialog
    var selectedComplaintForEdit by remember { mutableStateOf<Complaint?>(null) }
    var editStatus by remember { mutableStateOf("Pending") }
    var editRemarks by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF37474F))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Manage Complaints",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Search Bar
            OutlinedTextField(
                value = search,
                onValueChange = { viewModel.adminSearchQuery.value = it },
                placeholder = { Text("Search by title, student, roll...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        IconButton(onClick = { viewModel.adminSearchQuery.value = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Filter controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category Filter
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCategoryDropdownExpanded = true }
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        color = Color.Transparent
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Category: $categoryFilter",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand", modifier = Modifier.size(16.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = isCategoryDropdownExpanded,
                        onDismissRequest = { isCategoryDropdownExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, fontSize = 12.sp) },
                                onClick = {
                                    viewModel.adminCategoryFilter.value = cat
                                    isCategoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Status Filter
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isStatusDropdownExpanded = true }
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        color = Color.Transparent
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Status: $statusFilter",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand", modifier = Modifier.size(16.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = isStatusDropdownExpanded,
                        onDismissRequest = { isStatusDropdownExpanded = false }
                    ) {
                        statuses.forEach { stat ->
                            DropdownMenuItem(
                                text = { Text(stat, fontSize = 12.sp) },
                                onClick = {
                                    viewModel.adminStatusFilter.value = stat
                                    isStatusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // List of complaints
            if (filteredComplaints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Empty",
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No student complaints found.",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredComplaints) { complaint ->
                        val student = students.find { it.id == complaint.studentId }
                        AdminComplaintItemCard(
                            complaint = complaint,
                            student = student
                        ) {
                            selectedComplaintForEdit = complaint
                            editStatus = complaint.status
                            editRemarks = complaint.remarks
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }

    // Editor dialog for Admin Remarks and Status
    if (selectedComplaintForEdit != null) {
        val currentComplaint = selectedComplaintForEdit!!
        val student = students.find { it.id == currentComplaint.studentId }

        AlertDialog(
            onDismissRequest = { selectedComplaintForEdit = null },
            title = {
                Text(
                    text = "Update Status: COMP-${currentComplaint.id}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Title: ${currentComplaint.title}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Student: ${student?.name ?: "Unknown Student"} (${student?.rollNumber ?: "N/A"})", fontSize = 12.sp, color = Color.Gray)
                    Text(text = "Category: ${currentComplaint.category} | Location: ${currentComplaint.location}", fontSize = 12.sp, color = Color.Gray)
                    Text(text = "Description: ${currentComplaint.description}", fontSize = 12.sp, color = Color.DarkGray)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Set Operational Status", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Pending", "Under Review", "In Progress", "Resolved", "Rejected").forEach { statusChoice ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { editStatus = statusChoice }
                            ) {
                                RadioButton(
                                    selected = editStatus == statusChoice,
                                    onClick = { editStatus = statusChoice }
                                )
                                Text(statusChoice, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Add Administrator Remarks", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)

                    OutlinedTextField(
                        value = editRemarks,
                        onValueChange = { editRemarks = it },
                        placeholder = { Text("Write official comments or instructions...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateComplaintStatusAndRemarks(
                            currentComplaint.id,
                            editStatus,
                            editRemarks
                        ) { success, msg ->
                            onShowToast(msg)
                            selectedComplaintForEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            viewModel.adminDeleteComplaint(currentComplaint.id) { success, msg ->
                                onShowToast(msg)
                                selectedComplaintForEdit = null
                            }
                        }
                    ) {
                        Text("Delete", color = Color.Red)
                    }

                    TextButton(onClick = { selectedComplaintForEdit = null }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

@Composable
fun AdminComplaintItemCard(
    complaint: Complaint,
    student: Student?,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(complaint.createdDate))
    val statusColor = when (complaint.status) {
        "Pending" -> Color(0xFFFFB74D)
        "Under Review" -> Color(0xFFAB47BC)
        "In Progress" -> Color(0xFF26A69A)
        "Resolved" -> Color(0xFF66BB6A)
        else -> Color(0xFFE57373) // Rejected
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COMP-${complaint.id} | $dateStr",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = complaint.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = complaint.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Student Name & Roll
            Text(
                text = "Submitted by: ${student?.name ?: "Unknown"} (Roll: ${student?.rollNumber ?: "N/A"})",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = complaint.category,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .background(
                            when (complaint.priority) {
                                "High" -> Color(0xFFF44336).copy(alpha = 0.15f)
                                "Medium" -> Color(0xFFFF9800).copy(alpha = 0.15f)
                                else -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                            },
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = complaint.priority,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (complaint.priority) {
                            "High" -> Color(0xFFF44336)
                            "Medium" -> Color(0xFFFF9800)
                            else -> Color(0xFF4CAF50)
                        }
                    )
                }
            }

            if (complaint.remarks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Remarks: ${complaint.remarks}",
                    fontSize = 11.sp,
                    color = Color(0xFF388E3C),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AdminStudentManagementScreen(viewModel: MainViewModel, onShowToast: (String) -> Unit) {
    val search by viewModel.adminStudentSearchQuery.collectAsState()
    val filteredStudents by viewModel.filteredAdminStudents.collectAsState()
    val complaints by viewModel.allComplaints.collectAsState()

    var selectedStudentDetails by remember { mutableStateOf<Student?>(null) }
    var showDeleteConfirmForStudent by remember { mutableStateOf<Student?>(null) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF37474F))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Manage Registered Students",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Search input
            OutlinedTextField(
                value = search,
                onValueChange = { viewModel.adminStudentSearchQuery.value = it },
                placeholder = { Text("Search by name, roll, dept...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        IconButton(onClick = { viewModel.adminStudentSearchQuery.value = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (filteredStudents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No students found matching your criteria.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredStudents) { student ->
                        val studentCompCount = complaints.count { it.studentId == student.id }
                        StudentItemCard(
                            student = student,
                            complaintCount = studentCompCount,
                            onViewDetails = { selectedStudentDetails = student },
                            onDelete = { showDeleteConfirmForStudent = student }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }

    // Detail Dialog for Student and their complaints history
    if (selectedStudentDetails != null) {
        val student = selectedStudentDetails!!
        val studentComplaints = complaints.filter { it.studentId == student.id }

        AlertDialog(
            onDismissRequest = { selectedStudentDetails = null },
            title = {
                Text(
                    text = "Student Profile Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Name: ${student.name}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "Roll Number: ${student.rollNumber}", fontSize = 13.sp)
                    Text(text = "Department: ${student.department} | Year: ${student.year}", fontSize = 13.sp)
                    Text(text = "Mobile: ${student.phone}", fontSize = 13.sp)
                    Text(text = "Email: ${student.email}", fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "History of Complaints (${studentComplaints.size})",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )

                    if (studentComplaints.isEmpty()) {
                        Text("No complaints submitted by this student yet.", fontSize = 12.sp, color = Color.Gray)
                    } else {
                        studentComplaints.forEach { comp ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("COMP-${comp.id} | ${comp.category}", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                    Text(comp.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Status: ${comp.status}", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedStudentDetails = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
                ) {
                    Text("Close")
                }
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteConfirmForStudent != null) {
        val student = showDeleteConfirmForStudent!!
        AlertDialog(
            onDismissRequest = { showDeleteConfirmForStudent = null },
            title = { Text("Delete Student Record?") },
            text = { Text("Are you sure you want to delete ${student.name} and ALL of their historic complaint logs? This action is IRREVERSIBLE.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.adminDeleteStudent(student.id) { success, msg ->
                            onShowToast(msg)
                            showDeleteConfirmForStudent = null
                        }
                    }
                ) {
                    Text("Delete Student & Logs", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmForStudent = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StudentItemCard(
    student: Student,
    complaintCount: Int,
    onViewDetails: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Roll: ${student.rollNumber} | ${student.department}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Year: ${student.year} | Complaints: $complaintCount",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onViewDetails,
                    modifier = Modifier.background(Color(0xFFE0F7FA), CircleShape)
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Details", tint = Color(0xFF00ACC1))
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.background(Color(0xFFFFEBEE), CircleShape)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE53935))
                }
            }
        }
    }
}

@Composable
fun AdminSettingsScreen(viewModel: MainViewModel) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF37474F))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Settings & Utilities",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About section card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 580.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About CCMS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "College Complaint Management System (CCMS) is an offline-first mobile workspace solution engineered with Kotlin, Jetpack Compose, and Room Persistence Architecture. It empowers collegiate environments to streamline administrative tracking and students to express grievances transparently.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Version: 1.0.0 (Offline Stable)\nRelease Date: July 2026",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
