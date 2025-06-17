package com.nutomic.syncthingandroid.ui.screens.folders

import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nutomic.syncthingandroid.R
import com.nutomic.syncthingandroid.util.FileUtils
import com.nutomic.syncthingandroid.util.compose.openLocalDocumentTree.OpenLocalDocumentTreeContract
import kotlinx.coroutines.launch
import syncthingrest.model.folder.FolderID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderCreationScreen(
    viewModel: FolderCreationViewModel,
    onScreenExit: () -> Unit,
    initialFolderId: FolderID? = null,
    initialFolderLabel: String? = null,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver

    BackHandler {
        onScreenExit()
    }

    LaunchedEffect(initialFolderId, initialFolderLabel) {
        initialFolderId?.let {
            viewModel.updateFolderId(it)
        }
        initialFolderLabel?.let {
            viewModel.updateFolderName(it)
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.create_folder),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.createFolder()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text(stringResource(R.string.create))
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
            val dataState = uiState

            // Folder ID
            OutlinedTextField(
                value = dataState.id.value,
                onValueChange = { viewModel.updateFolderId(it) },
                label = { Text(stringResource(R.string.folder_id)) },
                isError = FolderCreationErrors.EmptyID in dataState.errors && dataState.id.value.isEmpty(),
                supportingText = {
                    if (FolderCreationErrors.EmptyID in dataState.errors) {
                        Text(
                            text = stringResource(R.string.folder_id_required),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Folder Path (read-only with select button)
            Text(stringResource(R.string.directory))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val folderSelectionIntentLauncher = rememberLauncherForActivityResult(
                    contract = OpenLocalDocumentTreeContract()
                ) { uri ->
                    uri?.let { uri ->
                        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        contentResolver.takePersistableUriPermission(uri, takeFlags)

                        val unixPath =
                            FileUtils.getAbsolutePathFromSAFUri(context, uri) ?: let {
                                Log.wtf(
                                    "FolderCreationScreen",
                                    "PANIC! THIS SHOULD NEVER HAPPEN! Unix path resolver returned null"
                                )
                                return@rememberLauncherForActivityResult
                            }
                        viewModel.updateFolderPath(unixPath)
                    }
                }

                Text(
                    text = if (FolderCreationErrors.EmptyDirectory in dataState.errors) {
                        "<please press the button>"
                    } else {
                        dataState.path.ifEmpty { "<none yet!>" }
                    },
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = {
                        folderSelectionIntentLauncher.launch(null)
                    },
                    colors = if (FolderCreationErrors.EmptyDirectory in dataState.errors) {
                        ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    } else {
                        ButtonDefaults.outlinedButtonColors()
                    }
                ) {
                    Text("Select directory")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Folder Label
            OutlinedTextField(
                value = dataState.label,
                onValueChange = { viewModel.updateFolderName(it) },
                label = { Text(stringResource(R.string.folder_label)) },
                modifier = Modifier.fillMaxWidth(),
                isError = FolderCreationErrors.EmptyLabel in dataState.errors && dataState.label.isEmpty(),
                supportingText = {
                    if (FolderCreationErrors.EmptyLabel in dataState.errors && dataState.label.isEmpty()) {
                        Text(stringResource(R.string.folder_label_required))
                    }
                }
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
                    value = when (dataState.type) {
                        FolderCreationScreenState.UIFolderType.Send -> stringResource(R.string.folder_type_send_only)
                        FolderCreationScreenState.UIFolderType.SendReceive -> stringResource(
                            R.string.folder_type_send_receive
                        )

                        FolderCreationScreenState.UIFolderType.ReceiveOnly -> stringResource(
                            R.string.folder_type_receive_only
                        )

                        FolderCreationScreenState.UIFolderType.ReceiveEncrypted -> stringResource(
                            R.string.folder_type_receive_encrypted
                        )
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
                    listOf(
                        FolderCreationScreenState.UIFolderType.SendReceive,
                        FolderCreationScreenState.UIFolderType.Send,
                        FolderCreationScreenState.UIFolderType.ReceiveOnly,
                        FolderCreationScreenState.UIFolderType.ReceiveEncrypted
                    ).forEach { folderType ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    when (folderType) {
                                        FolderCreationScreenState.UIFolderType.Send -> stringResource(
                                            R.string.folder_type_send_only
                                        )

                                        FolderCreationScreenState.UIFolderType.SendReceive -> stringResource(
                                            R.string.folder_type_send_receive
                                        )

                                        FolderCreationScreenState.UIFolderType.ReceiveOnly -> stringResource(
                                            R.string.folder_type_receive_only
                                        )

                                        FolderCreationScreenState.UIFolderType.ReceiveEncrypted -> stringResource(
                                            R.string.folder_type_receive_encrypted
                                        )
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
        }
    }
}
