package com.safesteps.app.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.safesteps.app.R
import com.safesteps.app.data.Contact
import com.safesteps.app.data.ContactsRepository
import com.safesteps.app.ui.components.EmptyStateMessage
import com.safesteps.app.ui.components.SafeStepsCard
import com.safesteps.app.ui.components.StatusPill
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen() {
    val context = LocalContext.current
    val repository = remember { ContactsRepository(context.applicationContext) }
    val contacts by repository.contacts.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<Contact?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        stringResource(id = R.string.contacts_title),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_contact)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = dimensionResource(id = R.dimen.screen_horizontal_padding))
        ) {
            Text(
                text = stringResource(id = R.string.contacts_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.spacing_small))
            )
            SafeStepsCard(
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.spacing_medium)),
                elevation = dimensionResource(id = R.dimen.contact_card_elevation)
            ) {
                StatusPill(
                    text = stringResource(id = R.string.contacts_status_title),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(id = R.string.contacts_status_desc, contacts.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_medium))
                )
            }

            if (contacts.isEmpty()) {
                EmptyContactsView()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(contacts) { contact ->
                        ContactCard(
                            contact = contact,
                            onCall = { openContactDialer(context, contact.phoneNumber) },
                            onEdit = { editingContact = contact },
                            onDelete = {
                                scope.launch {
                                    repository.deleteContact(contact.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddContactDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, phone, relationship, isPrimary ->
                scope.launch {
                    repository.addContact(
                        Contact(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            phoneNumber = phone,
                            relationship = relationship,
                            isPrimary = isPrimary
                        )
                    )
                }
                showAddDialog = false
            }
        )
    }

    if (editingContact != null) {
        val contact = editingContact!!
        EditContactDialog(
            contact = contact,
            onDismiss = { editingContact = null },
            onSave = { updatedContact ->
                scope.launch {
                    repository.updateContact(updatedContact)
                }
                editingContact = null
            }
        )
    }
}

@Composable
private fun EmptyContactsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        EmptyStateMessage(
            titleRes = R.string.no_contacts,
            bodyRes = R.string.add_contacts_hint,
            icon = Icons.Default.Person
        )
    }
}

@Composable
private fun ContactCard(
    contact: Contact,
    onCall: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(id = R.dimen.contact_card_elevation)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.spacing_large)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.contact_avatar_size))
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.trim().take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_medium)))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (contact.isPrimary) {
                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_small)))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(id = R.string.primary_contact_desc),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.height(dimensionResource(id = R.dimen.primary_icon_size))
                        )
                    }
                }
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (contact.relationship.isNotEmpty()) {
                    Text(
                        text = contact.relationship,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
            FilledTonalIconButton(onClick = onCall) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = stringResource(id = R.string.call_contact),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(dimensionResource(id = R.dimen.contact_action_icon_size))
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(id = R.string.edit_contact),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(dimensionResource(id = R.dimen.contact_action_icon_size))
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.delete_contact),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var isPrimary by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.add_contact_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(id = R.string.label_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(stringResource(id = R.string.label_phone)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text(stringResource(id = R.string.label_relationship)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(id = R.string.label_primary))
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isPrimary,
                        onCheckedChange = { isPrimary = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(name, phone, relationship, isPrimary) },
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) {
                Text(stringResource(id = R.string.btn_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.btn_cancel))
            }
        }
    )
}

@Composable
private fun EditContactDialog(
    contact: Contact,
    onDismiss: () -> Unit,
    onSave: (Contact) -> Unit
) {
    var name by remember { mutableStateOf(contact.name) }
    var phone by remember { mutableStateOf(contact.phoneNumber) }
    var relationship by remember { mutableStateOf(contact.relationship) }
    var isPrimary by remember { mutableStateOf(contact.isPrimary) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.edit_contact_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(id = R.string.label_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(stringResource(id = R.string.label_phone)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text(stringResource(id = R.string.label_relationship)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(id = R.string.label_primary))
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isPrimary,
                        onCheckedChange = { isPrimary = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        contact.copy(
                            name = name,
                            phoneNumber = phone,
                            relationship = relationship,
                            isPrimary = isPrimary
                        )
                    )
                },
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) {
                Text(stringResource(id = R.string.btn_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.btn_cancel))
            }
        }
    )
}

private fun openContactDialer(context: Context, phoneNumber: String) {
    if (phoneNumber.none { it.isDigit() }) {
        Toast.makeText(
            context,
            context.getString(R.string.invalid_phone_number),
            Toast.LENGTH_LONG
        ).show()
        return
    }

    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:${Uri.encode(phoneNumber)}")
    }

    try {
        context.startActivity(intent)
    } catch (exception: ActivityNotFoundException) {
        Toast.makeText(
            context,
            context.getString(R.string.alert_app_missing),
            Toast.LENGTH_LONG
        ).show()
    }
}
