package com.composeapp.signzy_android_assignment.presentation.home

import android.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.room.util.TableInfo
import coil.compose.AsyncImage
import com.composeapp.signzy_android_assignment.domain.models.User
import com.composeapp.signzy_android_assignment.domain.models.VerificationStatus
import com.composeapp.signzy_android_assignment.presentation.MainViewModel
import com.composeapp.signzy_android_assignment.presentation.navigation.AppGraph
import com.composeapp.signzy_android_assignment.presentation.state.ResultState
import kotlin.random.Random


enum class KycStatus { PENDING, VERIFIED }
enum class AccountType { ALL, SAVINGS, CURRENT, NRI }

data class Customer(
    val name: String,
    val accountNumber: String,
    val balance: String,
    val status: KycStatus,
    val accountType: AccountType,
    val avatarResId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, navHostController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFilter by remember { mutableStateOf(AccountType.ALL) }
    val searchQuery = rememberTextFieldState()

    val users by viewModel.users.collectAsStateWithLifecycle()




    val customers = remember {
        listOf(
            Customer(
                "Arjun Sharma",
                "**** 8890",
                "Rs. 45,200",
                KycStatus.PENDING,
                AccountType.SAVINGS,
                0
            ),
            Customer(
                "Priya Singh",
                "**** 1123",
                "Rs. 1,12,500",
                KycStatus.PENDING,
                AccountType.CURRENT,
                0
            ),
            Customer(
                "Rahul Verma",
                "**** 5567",
                "Rs. 28,900",
                KycStatus.VERIFIED,
                AccountType.SAVINGS,
                0
            )
        )
    }

    // Filter Logic
    val filteredCustomers = customers.filter { customer ->
        val matchesTab =
            if (selectedTab == 0) customer.status == KycStatus.PENDING else customer.status == KycStatus.VERIFIED
        val matchesFilter =
            selectedFilter == AccountType.ALL || customer.accountType == selectedFilter
        val matchesSearch = customer.name.contains(
            searchQuery.text,
            ignoreCase = true
        ) || customer.accountNumber.contains(searchQuery.text)
        matchesTab && matchesFilter && matchesSearch
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            "Digital Bank",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "${customers.size} customers",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { paddingValues ->


        when (val state = users.resultState) {

            is ResultState.Success -> {
                val userList = state.data
                val verifiedUserList = (viewModel.userVerification.collectAsState().value.resultState as ResultState.Success).data

                val verifiedCount = verifiedUserList.count { it.verificationStatus == VerificationStatus.APPROVED }
                val pendingCount = userList.size - verifiedCount

                val onlyVerified = userList.filter { user ->
                    verifiedUserList.find { it.userId == user.id } != null
                }

                val onlyUnVersion = userList.filter {user ->
                    verifiedUserList.find { it.userId == user.id } == null
                }




                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    item {
                        TextField(
                            state = searchQuery,

                            placeholder = {
                                Text(
                                    "Search Customers by Name or Account Number",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            colors = TextFieldDefaults.colors(),
                            lineLimits = TextFieldLineLimits.SingleLine
                        )

                    }

                    item {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            divider = { HorizontalDivider(color = Color(0xFFE1E2E5)) }
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Pending", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                                        Badge(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White) { Text("$pendingCount",
                                            color = MaterialTheme.colorScheme.onPrimary) }
                                    }
                                }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Verified", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                                        Badge(containerColor = Color(0xFFE1E2E5), contentColor = MaterialTheme.colorScheme.primary) { Text("$verifiedCount", color = MaterialTheme.colorScheme.onPrimary) }
                                    }
                                }
                            )
                        }
                    }

                    if (userList.isNotEmpty()) {

                        val list = if(selectedTab == 0) onlyUnVersion else onlyVerified

                        items(list) {
                            if(searchQuery.text.isNotEmpty() && !it.firstName.contains(searchQuery.text, ignoreCase = true)) return@items

                            UserCard(it,selectedTab == 1, onClick = {
                                navHostController.navigate(AppGraph.Profile(it))
                            }) {
                                navHostController.navigate(AppGraph.KycScreen(it))
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "No Customers Found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                    }


                }
            }

            is ResultState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Loading...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

            }

            is ResultState.Error -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.SentimentVeryDissatisfied,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )

                    Text(
                        text = "Something went wrong",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )


                    Button(onClick = {
                        viewModel.fetchUsers()
                    }) {
                        Text(text = "Retry")
                    }


                }

            }


        }

    }
}

@Composable
fun UserCard(user: User, isVerified: Boolean = false, onClick: (id: Int) -> Unit, doKyc: (id: Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(user.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(),
        border = borderStrokeFromColor(Color(0xFFE1E2E5))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))

            ) {

                AsyncImage(
                    model = user.image,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${user.firstName} ${user.lastName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )


                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if(isVerified) Color(0xFF166534) else MaterialTheme.colorScheme.secondary)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Pending",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if(isVerified) Color.White else MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                Text(
                    text = user.bank.cardNumber,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = "Rs. ${Random(10000).nextInt().toString()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (!isVerified) {
                Button(
                    onClick = { doKyc(user.id) },
                    colors = ButtonDefaults.buttonColors(),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Do KYC", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            } else {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Verified Identity Checked",
                    tint = Color(0xFF166534),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                )
            }
        }
    }
}

fun borderStrokeFromColor(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)


