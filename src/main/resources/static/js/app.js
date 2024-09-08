$(document).ready(function () {
    function loadUsers() {
        fetch('/admin/api/users')
            .then(response => response.json())
            .then(users => {
                $('#userTableBody').empty();
                users.forEach(user => {
                    let roles = user.roles.map(role => role.name).join(', ');

                    $('#userTableBody').append(`
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.firstName}</td>
                            <td>${user.lastName}</td>
                            <td>${user.age}</td>
                            <td>${user.email}</td>
                            <td>${roles}</td>
                            <td><button class="btn btn-primary btn-sm edit-btn" data-id="${user.id}">Edit</button></td>
                            <td><button class="btn btn-danger btn-sm delete-btn" data-id="${user.id}">Delete</button></td>
                        </tr>
                    `);
                });

                $('.edit-btn').on('click', function () {
                    let userId = $(this).data('id');
                    openEditModal(userId);
                });

                $('.delete-btn').on('click', function () {
                    let userId = $(this).data('id');
                    openDeleteModal(userId);
                });
            });
    }

    function loadRoles(selectId, userRoles = []) {
        fetch('/admin/api/roles')
            .then(response => response.json())
            .then(allRoles => {
                const $select = $(selectId);
                $select.empty();
                allRoles.forEach(role => {
                    let selected = userRoles.some(userRole => userRole.name === role.name) ? 'selected' : '';
                    $select.append(`<option value="${role.id}" ${selected}>${role.name}</option>`);
                });
            });
    }

    function openEditModal(userId) {
        fetch(`/admin/api/users/${userId}`)
            .then(response => response.json())
            .then(user => {
                $('#idEdit').val(user.id);
                $('#firstNameEdit').val(user.firstName);
                $('#lastNameEdit').val(user.lastName);
                $('#ageEdit').val(user.age);
                $('#emailEdit').val(user.email);

                loadRoles('#roleEdit', user.roles);

                $('#editModal').modal('show');

                $('#editUserForm').off('submit');

                $('#editUserForm').on('submit', function (event) {
                    event.preventDefault();

                    let selectedRoles = $('#roleEdit').val().map(roleId => ({ id: roleId }));

                    let updatedUser = {
                        email: $('#emailEdit').val(),
                        password: $('#passwordEdit').val(),
                        firstName: $('#firstNameEdit').val(),
                        lastName: $('#lastNameEdit').val(),
                        age: $('#ageEdit').val(),
                        roles: selectedRoles,
                    };

                    fetch(`/admin/api/users/${userId}`, {
                        method: 'PATCH',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(updatedUser),
                    }).then(response => {
                        if (response.ok) {
                            $('#editModal').modal('hide');
                            loadUsers();
                        } else {
                            alert('Error when updating user');
                        }
                    }).catch(error => console.error('Ошибка:', error));
                });
            })
            .catch(error => console.error('Error:', error));
    }

    function openDeleteModal(userId) {
        fetch(`/admin/api/users/${userId}`)
            .then(response => response.json())
            .then(user => {
                $('#idDelete').val(user.id);
                $('#firstNameDelete').val(user.firstName);
                $('#lastNameDelete').val(user.lastName);
                $('#ageDelete').val(user.age);
                $('#emailDelete').val(user.email);

                loadRoles('#roleDelete', user.roles);

                $('#deleteModal').modal('show');

                $('#deleteUserForm').off('submit');

                $('#deleteUserForm').on('submit', function (event) {
                    event.preventDefault();
                    fetch(`/admin/api/users/${userId}`, {
                        method: 'DELETE',
                    }).then(() => {
                        $('#deleteModal').modal('hide');
                        loadUsers();
                    });
                });
            });
    }

    $('#addUserForm').on('submit', function (event) {
        event.preventDefault();
        let selectedRoles = $('#roleAdd').val().map(roleId => ({ id: roleId }));
        let newUser = {
            firstName: $('#firstName').val(),
            lastName: $('#lastName').val(),
            age: $('#age').val(),
            email: $('#email').val(),
            password: $('#password').val(),
            roles: selectedRoles,
        };

        fetch('/admin/api/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(newUser),
        }).then(response => {
            if (response.ok) {
                $('#addUserForm')[0].reset();
                loadUsers();
            } else {
                alert('Error when adding a user');
            }
        }).catch(error => console.error('Error:', error));
    });

    loadRoles('#roleAdd');
    loadUsers();
});