package com.composeapp.signzy_android_assignment.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.composeapp.signzy_android_assignment.presentation.MainViewModel
import com.composeapp.signzy_android_assignment.presentation.state.ResultState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.composeapp.signzy_android_assignment.presentation.navigation.AppGraph
import io.ktor.http.ContentType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Int,
    viewModel: MainViewModel,
    navHostController: NavHostController
) {

    val userProfile =
        (viewModel.users.collectAsState().value.resultState as ResultState.Success).data.find {
            it.id == userId
        }

    val userVerification =
        (viewModel.userVerification.collectAsState().value.resultState as ResultState.Success).data.find {
            it.userId == userId
        }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Account Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(),

                    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            AsyncImage(
                                model = userProfile?.image,
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${userProfile?.firstName} ${userProfile?.lastName}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(text = "PENDING", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "${userProfile?.bank?.cardNumber}", fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Financial Balance Metrics Info
                        Text(text = "Available Balance", fontSize = 12.sp)
                        Text(text = "Rs. 45,200", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                InfoSectionContainer(title = "Profile") {
                    InfoRowItem(label = "Date of Birth", value = userProfile?.birthDate.toString())
                    InfoRowItem(
                        label = "Nationality",
                        value = userProfile?.address?.country.toString()
                    )
                    InfoRowItem(
                        label = "Address",
                        value = userProfile?.address?.address.toString(),
                        isLongText = true
                    )
                    InfoRowItem(label = "Contact", value = userProfile?.phone.toString())
                }
            }

            item {
                InfoSectionContainer(title = "Bank Details") {
                    InfoRowItem(label = "Bank/Branch", value = userProfile?.bank?.iban.toString())
                    InfoRowItem(label = "IFSC", value = userProfile?.bank?.cardType.toString())
                }
            }


            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text("KYC Selfie", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Information detailing metric requirements",
                                modifier = Modifier.size(16.dp)
                            )

                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(4f / 3f)
                                .clip(RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE1E2E5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (
                                        userVerification != null
                                    ) {
                                        AsyncImage(
                                            model = userVerification.capturedPhoto,
                                            contentDescription = "Captured Selfie",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )

                                    } else {
                                        Icon(
                                            Icons.Default.PhotoCamera,
                                            contentDescription = null,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Liveness detection photo required for verification.",
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                navHostController.navigate(AppGraph.KycScreen(userId))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Face, contentDescription = null)
                                Text(
                                    if (userVerification != null) "Retake" else "do Kyc",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun InfoSectionContainer(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
//                    .background(SurfaceContainerLow)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                content = content
            )
        }
    }
}

@Composable
fun InfoRowItem(label: String, value: String, isLongText: Boolean = false) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = if (isLongText) Alignment.Top else Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(if (isLongText) 1.2f else 1.8f)
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    }
}
