package com.nutomic.syncthingandroid.ui.screens.folders

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.nutomic.syncthingandroid.R
import kotlinx.coroutines.launch
import syncthingrest.model.folder.FolderID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderSettingsScreen(
    viewModel: FolderSettingsViewModel,
    folderId: FolderID,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(folderId) {
        viewModel.loadFolder(folderId)
    }

    Scaffold(
        topBar = {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(R.string.edit_folder),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                if (uiState is FolderSettingsScreenState.Success) {
                    Button(
                        onClick = { viewModel.saveChanges() },
                        modifier = Modifier
                            .padding(bottom = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text("Update")
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) then modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                FolderSettingsScreenState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                FolderSettingsScreenState.FolderNotFound -> {
                    Text(
                        text = stringResource(R.string.folder_list_empty), // Reusing string for "not found"
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                FolderSettingsScreenState.FailedToLoad -> {
                    Text(
                        text = stringResource(R.string.generic_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is FolderSettingsScreenState.Success -> {
                    val successState = uiState as FolderSettingsScreenState.Success
                    val clipboardManager = LocalClipboard.current
                    val coroutineScope = rememberCoroutineScope()
                    var showDeletionDialogue by remember {
                        mutableStateOf(false)
                    }

                    // Folder Path (read-only)
                    Text(
                        text = successState.path,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Folder ID (read-only)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = successState.id.value,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(onClick = {
                            val clipData = ClipData.newPlainText(
                                successState.id.value,
                                successState.id.value
                            )
                            val clipEntry = ClipEntry(clipData)
                            coroutineScope.launch {
                                clipboardManager.setClipEntry(clipEntry)
                            }
                        }) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy ID icon"
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Copy ID")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))


                    // Folder Label
                    OutlinedTextField(
                        value = successState.label,
                        onValueChange = { viewModel.updateFolderName(it) },
                        label = { Text(stringResource(R.string.folder_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))



                    // Folder Type
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = when (successState.type) {
                                FolderSettingsScreenState.UIFolderType.Send -> stringResource(R.string.folder_type_send_only)
                                FolderSettingsScreenState.UIFolderType.SendReceive -> stringResource(R.string.folder_type_send_receive)
                                FolderSettingsScreenState.UIFolderType.ReceiveOnly -> stringResource(R.string.folder_type_receive_only)
                            },
                            onValueChange = { /* Read-only */ },
                            readOnly = true,
                            label = { Text(stringResource(R.string.folder_type)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor(type = MenuAnchorType.PrimaryEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            // Only allow Send, SendReceive, ReceiveOnly for existing folders
                            listOf(
                                FolderSettingsScreenState.UIFolderType.SendReceive,
                                FolderSettingsScreenState.UIFolderType.Send,
                                FolderSettingsScreenState.UIFolderType.ReceiveOnly
                            ).forEach { folderType ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (folderType) {
                                                FolderSettingsScreenState.UIFolderType.Send -> stringResource(R.string.folder_type_send_only)
                                                FolderSettingsScreenState.UIFolderType.SendReceive -> stringResource(R.string.folder_type_send_receive)
                                                FolderSettingsScreenState.UIFolderType.ReceiveOnly -> stringResource(R.string.folder_type_receive_only)
                                            }
                                        )
                                    },
                                    onClick = {
                                        viewModel.updateFolderType(folderType)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Paused State
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                successState.paused,
                                role = Role.Switch,
                                onValueChange = { viewModel.updateFolderPausedState(it) }
                            )
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(R.string.folder_pause))
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = successState.paused,
                            onCheckedChange = null
                        )
                    }

                    // Deletion button
                    Button(
                        onClick = { showDeletionDialogue = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text(stringResource(R.string.delete_folder))
                    }

                    if (showDeletionDialogue) {
                        AlertDialog(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = null
                                )
                            },
                            title = {
                                Text(
                                    text = stringResource(R.string.delete_folder)
                                )
                            },
                            text = {
                                Text(
                                    text = "Are you sure you want to delete this folder?"
                                )
                            },
                            onDismissRequest = {
                                showDeletionDialogue = false
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.deleteFolder()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    )
                                ) {
                                    Text(
                                        text = "Delete"
                                    )
                                }
                            },
                            dismissButton = {
                                OutlinedButton(
                                    onClick = {
                                        showDeletionDialogue = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                ) {
                                    Text(
                                        text = "Nevermind"
                                    )
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            iconContentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}
