package com.example.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Complaint
import com.example.data.Student
import com.example.ui.MainViewModel
import com.example.ui.Screen
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudentDashboardScreen(viewModel: MainViewModel, onShowToast: (String) -> Unit) {
    val student by viewModel.loggedInStudent.collectAsState()
    val complaints by viewModel.allComplaints.collectAsState()

    val studentComplaints = student?.let { s ->
        complaints.filter { it.studentId == s.id }
    } ?: emptyList()

    val total = studentComplaints.size
    val pending = studentComplaints.count { it.status == "Pending" }
    val underReview = studentComplaints.count { it.status == "Under Review" }
    val inProgress = studentComplaints.count { it.status == "In Progress" }
    val resolved = studentComplaints.count { it.status == "Resolved" }

    val recentComplaints = studentComplaints.take(3)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo(Screen.SubmitComplaint) },
                containerColor = Color(0xFF6750A4),
                contentColor = Color.White,
                modifier = Modifier.testTag("add_complaint_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Submit Complaint")
            }
        }
    ) { padding ->
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
                                text = "Student Portal",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // User initials badge
                            val initials = student?.name?.split(" ")?.filter { it.isNotEmpty() }?.map { it.first() }?.joinToString("")?.take(2)?.uppercase() ?: "ST"
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFEADDFF), CircleShape)
                                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials,
                                    color = Color(0xFF21005D),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                    .testTag("student_logout_button")
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
                        text = student?.name ?: "Student Name",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Roll No: ${student?.rollNumber ?: "N/A"}  •  ${student?.department ?: "N/A"} (${student?.year ?: "N/A"})",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Statistics Panel (4 Columns matching Design HTML precisely)
            Text(
                text = "Dashboard Analytics",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatMetricCard(
                    label = "Total",
                    count = total,
                    bgColor = Color(0xFFEADDFF),
                    textColor = Color(0xFF21005D),
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    label = "Pending",
                    count = pending,
                    bgColor = Color(0xFFF2B8B5),
                    textColor = Color(0xFF601410),
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    label = "Active",
                    count = underReview + inProgress,
                    bgColor = Color(0xFFD1E1FF),
                    textColor = Color(0xFF001D35),
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    label = "Done",
                    count = resolved,
                    bgColor = Color(0xFFC2E7CB),
                    textColor = Color(0xFF072711),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Actions Grid
            Text(
                text = "Quick Actions",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    title = "Submit New",
                    icon = Icons.Default.Add,
                    color = Color(0xFFD0BCFF),
                    tint = Color(0xFF21005D),
                    onClick = { viewModel.navigateTo(Screen.SubmitComplaint) },
                    modifier = Modifier.weight(1f)
                )
                ActionCard(
                    title = "History",
                    icon = Icons.Default.History,
                    color = Color(0xFFEADDFF),
                    tint = Color(0xFF21005D),
                    onClick = { viewModel.navigateTo(Screen.ComplaintHistory) },
                    modifier = Modifier.weight(1f)
                )
                ActionCard(
                    title = "My Profile",
                    icon = Icons.Default.Person,
                    color = Color(0xFFEADDFF),
                    tint = Color(0xFF21005D),
                    onClick = { viewModel.navigateTo(Screen.StudentProfile) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Recent Complaints Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Complaints",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20)
                )
                Text(
                    text = "View All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6750A4),
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.ComplaintHistory) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (recentComplaints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Empty",
                            tint = Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No complaints submitted yet.",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    recentComplaints.forEach { complaint ->
                        ComplaintItemRow(complaint = complaint) {
                            viewModel.navigateTo(Screen.ComplaintDetails(complaint.id))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun StatMetricCard(label: String, count: Int, bgColor: Color, textColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format("%02d", count),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = textColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ActionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, tint: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = tint)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun ComplaintItemRow(complaint: Complaint, onClick: () -> Unit) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(complaint.createdDate))
    
    val statusBg = when (complaint.status) {
        "Pending" -> Color(0xFFF2B8B5)
        "Under Review", "In Progress" -> Color(0xFFD1E1FF)
        "Resolved" -> Color(0xFFC2E7CB)
        else -> Color(0xFFF2B8B5) // Rejected / others
    }

    val statusText = when (complaint.status) {
        "Pending" -> Color(0xFF601410)
        "Under Review", "In Progress" -> Color(0xFF001D35)
        "Resolved" -> Color(0xFF072711)
        else -> Color(0xFF601410)
    }

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
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Status colored pill
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(statusBg)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COMP-${complaint.id}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                    
                    // Status Tag: white background with border
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = complaint.status.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = complaint.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${complaint.category}  •  $dateStr",
                        fontSize = 11.sp,
                        color = Color(0xFF49454F)
                    )
                }
            }
        }
    }
}

@Composable
fun SubmitComplaintScreen(
    viewModel: MainViewModel,
    editingComplaintId: Long? = null,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    var isEditing = editingComplaintId != null

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Academic") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var location by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var existingImagePath by remember { mutableStateOf<String?>(null) }

    // Dropdown expanded state
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Academic", "Hostel", "Library", "Canteen", "Transport", "Laboratory",
        "Wi-Fi", "Electrical", "Water Supply", "Sports", "Cleanliness", "Security", "Others"
    )

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
        }
    }

    // Load original complaint if editing
    LaunchedEffect(editingComplaintId) {
        if (isEditing && editingComplaintId != null) {
            val complaint = viewModel.repository.getComplaintByIdDirect(editingComplaintId)
            if (complaint != null) {
                title = complaint.title
                category = complaint.category
                description = complaint.description
                priority = complaint.priority
                location = complaint.location
                existingImagePath = complaint.imagePath
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E88E5))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isEditing) "Edit Complaint" else "Submit New Complaint",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 580.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Complaint Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Complaint Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Category Selection Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            label = { Text("Category") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded }) {
                                    Icon(
                                        imageVector = if (isCategoryDropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Dropdown"
                                    )
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = isCategoryDropdownExpanded,
                            onDismissRequest = { isCategoryDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        isCategoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Priority selection
                    Column {
                        Text("Priority Level", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            listOf("Low", "Medium", "High").forEach { pr ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { priority = pr }
                                ) {
                                    RadioButton(
                                        selected = priority == pr,
                                        onClick = { priority = pr }
                                    )
                                    Text(pr, fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    // Location Input
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location (e.g. Block C, Room 204)") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Location") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Description Input
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detailed Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )

                    // Image selector
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .clickable { galleryLauncher.launch("image/*") }
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (imageUri != null) "Change Selected Image" else if (existingImagePath != null) "Change Current Attachment" else "Attach Image (Optional)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                        if (imageUri != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Selected Image URI: ${imageUri!!.path?.takeLast(20)}",
                                fontSize = 11.sp,
                                color = Color.Blue
                            )
                        } else if (existingImagePath != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Attached Image Saved",
                                fontSize = 11.sp,
                                color = Color(0xFF388E3C)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (title.isBlank() || description.isBlank() || location.isBlank()) {
                                onShowToast("Please enter Title, Description and Location!")
                                return@Button
                            }

                            if (isEditing && editingComplaintId != null) {
                                viewModel.editPendingComplaint(
                                    editingComplaintId,
                                    title.trim(),
                                    category,
                                    description.trim(),
                                    priority,
                                    location.trim(),
                                    imageUri
                                ) { success, msg ->
                                    onShowToast(msg)
                                }
                            } else {
                                viewModel.submitComplaint(
                                    title.trim(),
                                    category,
                                    description.trim(),
                                    priority,
                                    location.trim(),
                                    imageUri
                                ) { success, msg ->
                                    onShowToast(msg)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_complaint_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                    ) {
                        Text(
                            text = if (isEditing) "SAVE CHANGES" else "SUBMIT COMPLAINT",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ComplaintHistoryScreen(viewModel: MainViewModel) {
    val search by viewModel.studentSearchQuery.collectAsState()
    val categoryFilter by viewModel.studentCategoryFilter.collectAsState()
    val statusFilter by viewModel.studentStatusFilter.collectAsState()
    val sortNewest by viewModel.studentSortNewest.collectAsState()

    val filteredComplaints by viewModel.filteredStudentComplaints.collectAsState()

    val categories = listOf(
        "All", "Academic", "Hostel", "Library", "Canteen", "Transport", "Laboratory",
        "Wi-Fi", "Electrical", "Water Supply", "Sports", "Cleanliness", "Security", "Others"
    )

    val statuses = listOf("All", "Pending", "Under Review", "In Progress", "Resolved", "Rejected")

    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }

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
                    .background(Color(0xFF1E88E5))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "My Complaints",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Export to PDF button
                IconButton(
                    onClick = { viewModel.exportMyComplaints() },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = "Export to PDF", tint = Color.White)
                }
            }

            // Search Bar
            OutlinedTextField(
                value = search,
                onValueChange = { viewModel.studentSearchQuery.value = it },
                placeholder = { Text("Search by title...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        IconButton(onClick = { viewModel.studentSearchQuery.value = "" }) {
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

            // Filtering Controls
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
                                    viewModel.studentCategoryFilter.value = cat
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
                                    viewModel.studentStatusFilter.value = stat
                                    isStatusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Sort toggle
                IconButton(
                    onClick = { viewModel.studentSortNewest.value = !sortNewest },
                    modifier = Modifier
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = if (sortNewest) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = "Sort direction",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Complaints list
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
                            text = "No matching complaints found.",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = "Try adjusting your search query or filters.",
                            fontSize = 13.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
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
                        ComplaintItemRow(complaint = complaint) {
                            viewModel.navigateTo(Screen.ComplaintDetails(complaint.id))
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun ComplaintDetailsScreen(
    viewModel: MainViewModel,
    complaintId: Long,
    onShowToast: (String) -> Unit
) {
    val complaints by viewModel.allComplaints.collectAsState()
    val complaint = complaints.find { it.id == complaintId }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (complaint == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val createdDateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(complaint.createdDate))
    val updatedDateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(complaint.updatedDate))

    val statusColor = when (complaint.status) {
        "Pending" -> Color(0xFFFFB74D)
        "Under Review" -> Color(0xFFAB47BC)
        "In Progress" -> Color(0xFF26A69A)
        "Resolved" -> Color(0xFF66BB6A)
        else -> Color(0xFFE57373) // Rejected
    }

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
                    .background(Color(0xFF1E88E5))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Complaint Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Card
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "COMP-${complaint.id}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )

                        // Status Badge
                        Box(
                            modifier = Modifier
                                .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = complaint.status,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = complaint.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Priority, Location, Category pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoPill(label = "Category", value = complaint.category, color = Color(0xFF1E88E5))
                        InfoPill(
                            label = "Priority",
                            value = complaint.priority,
                            color = when (complaint.priority) {
                                "High" -> Color(0xFFF44336)
                                "Medium" -> Color(0xFFFF9800)
                                else -> Color(0xFF4CAF50)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Location detail
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Location: ${complaint.location}", fontSize = 13.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dates detail
                    Text(text = "Submitted: $createdDateStr", fontSize = 12.sp, color = Color.LightGray)
                    if (complaint.createdDate != complaint.updatedDate) {
                        Text(text = "Last Updated: $updatedDateStr", fontSize = 12.sp, color = Color.LightGray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description text
                    Text(text = "Description", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = complaint.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    // Image attachment if present
                    if (complaint.imagePath != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Attachment", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        LocalImageViewer(filePath = complaint.imagePath)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timeline Tracker Section (Polished UI)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Complaint Lifecycle Timeline", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    val steps = listOf("Pending", "Under Review", "In Progress", "Resolved/Rejected")
                    val currentStepIndex = when (complaint.status) {
                        "Pending" -> 0
                        "Under Review" -> 1
                        "In Progress" -> 2
                        "Resolved", "Rejected" -> 3
                        else -> 0
                    }

                    Column {
                        steps.forEachIndexed { idx, step ->
                            val isActive = idx <= currentStepIndex
                            val isCompleted = idx < currentStepIndex
                            val stepColor = if (isActive) {
                                if (step == "Resolved/Rejected" && complaint.status == "Rejected") Color(0xFFF44336)
                                else Color(0xFF388E3C)
                            } else Color.LightGray

                            Row(modifier = Modifier.height(60.dp)) {
                                // Draw timeline node line and circle
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(32.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(stepColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isCompleted) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = "Check", tint = Color.White, modifier = Modifier.size(14.dp))
                                        } else {
                                            Box(modifier = Modifier.size(8.dp).background(Color.White, CircleShape))
                                        }
                                    }
                                    if (idx < steps.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .fillMaxHeight()
                                                .background(if (idx < currentStepIndex) Color(0xFF388E3C) else Color.LightGray)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                                    Text(
                                        text = if (step == "Resolved/Rejected") {
                                            if (complaint.status == "Rejected") "Rejected" else "Resolved"
                                        } else step,
                                        fontSize = 13.sp,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isActive) MaterialTheme.colorScheme.onSurface else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Admin Remarks Card (only show if admin entered anything or if status changed)
            if (complaint.remarks.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = BorderStroke(0.5.dp, Color(0xFF81C784))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = "Remark", tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Official Admin Remarks", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = complaint.remarks,
                            fontSize = 13.sp,
                            color = Color(0xFF1B5E20),
                            lineHeight = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Edit / Delete Actions (Only visible if Status is Pending)
            if (complaint.status == "Pending") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(Screen.SubmitComplaint) }, // Will pre-populate for editing
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Complaint")
                    }

                    Button(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete")
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Complaint") },
            text = { Text("Are you sure you want to delete this pending complaint permanently?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deletePendingComplaint(complaint.id) { success, msg ->
                            onShowToast(msg)
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun InfoPill(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
            .border(0.5.dp, color.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$label: $value",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
fun LocalImageViewer(filePath: String) {
    val file = File(filePath)
    if (file.exists()) {
        val bitmap = try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            null
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Complaint Image Attachment",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            return
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Warning, contentDescription = "Alert", tint = Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Attached image not found", color = Color.Gray)
    }
}

@Composable
fun StudentProfileScreen(viewModel: MainViewModel, onShowToast: (String) -> Unit) {
    val student by viewModel.loggedInStudent.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(student) {
        student?.let { s ->
            name = s.name
            phone = s.phone
            department = s.department
            year = s.year
            password = s.password
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E88E5))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "My Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Info Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 580.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Edit Personal Information",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = student?.rollNumber ?: "",
                        onValueChange = {},
                        label = { Text("Roll Number (Read-only)") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = "Roll") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    OutlinedTextField(
                        value = student?.email ?: "",
                        onValueChange = {},
                        label = { Text("Email Address (Read-only)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Mobile Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = department,
                            onValueChange = { department = it },
                            label = { Text("Department") },
                            leadingIcon = { Icon(Icons.Default.Business, contentDescription = "Dept") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = year,
                            onValueChange = { year = it },
                            label = { Text("Year") },
                            leadingIcon = { Icon(Icons.Default.Class, contentDescription = "Year") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Change Password",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("New Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Pass") },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle"
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) PasswordVisualTransformation() else PasswordVisualTransformation(), // Keep masked or toggle based on variable
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Save Button
                    Button(
                        onClick = {
                            if (name.isBlank() || phone.isBlank() || department.isBlank() || year.isBlank() || password.isBlank()) {
                                onShowToast("Please fill all editable fields!")
                                return@Button
                            }
                            if (password.length < 6) {
                                onShowToast("Password must be at least 6 characters!")
                                return@Button
                            }

                            student?.let { original ->
                                val updated = original.copy(
                                    name = name.trim(),
                                    phone = phone.trim(),
                                    department = department.trim(),
                                    year = year.trim(),
                                    password = password
                                )
                                viewModel.updateStudentProfile(updated) { success, msg ->
                                    onShowToast(msg)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_profile_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                    ) {
                        Text(
                            text = "SAVE CHANGES",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
