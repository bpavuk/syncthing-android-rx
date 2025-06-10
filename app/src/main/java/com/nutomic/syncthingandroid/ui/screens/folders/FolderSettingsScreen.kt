package com.nutomic.syncthingandroid.ui.screens.folders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nutomic.syncthingandroid.R
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) then modifier
    ) {
        Text(
            text = stringResource(R.string.edit_folder),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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

                // Update button
                Button(
                    onClick = { viewModel.saveChanges() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Update")
                }

                // Folder ID (read-only)
                OutlinedTextField(
                    value = successState.id.value,
                    onValueChange = { /* Read-only */ },
                    label = { Text(stringResource(R.string.folder_id)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Folder Path (read-only)
                OutlinedTextField(
                    value = successState.path,
                    onValueChange = { /* Read-only */ },
                    label = { Text(stringResource(R.string.directory)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
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
                Spacer(modifier = Modifier.height(16.dp))

                // Paused State
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.folder_pause))
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(
                        checked = successState.paused,
                        onCheckedChange = { viewModel.updateFolderPausedState(it) }
                    )
                }
            }
        }
    }
}
