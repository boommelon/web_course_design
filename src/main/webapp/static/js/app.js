$(function () {
    var basePath = typeof contextPath === "string" ? contextPath : "";

    $(".sidebar a").each(function () {
        var linkPath = this.pathname;
        if (linkPath === window.location.pathname) {
            $(this).addClass("active");
        }
    });

    function showAjaxError(xhr, fallbackMessage) {
        var message = fallbackMessage;
        if (xhr.responseJSON && xhr.responseJSON.message) {
            message = xhr.responseJSON.message;
        }
        alert(message);
    }

    $(".ajax-delete-user").on("click", function () {
        if (!confirm("确定要删除该用户吗？")) {
            return;
        }

        var $button = $(this);
        var id = $button.data("id");

        $button.prop("disabled", true);
        $.ajax({
            url: basePath + "/api/user/delete.action",
            type: "POST",
            dataType: "json",
            data: { id: id },
            success: function (res) {
                if (res && res.success) {
                    $("tr[data-user-id='" + id + "']").fadeOut(300, function () {
                        $(this).remove();
                    });
                } else {
                    $button.prop("disabled", false);
                    alert((res && res.message) || "删除失败");
                }
            },
            error: function (xhr) {
                $button.prop("disabled", false);
                showAjaxError(xhr, "删除失败，请稍后重试");
            }
        });
    });

    $(".ajax-delete-announcement").on("click", function () {
        if (!confirm("确定删除该公告？")) {
            return;
        }

        var $button = $(this);
        var id = $button.data("id");

        $button.prop("disabled", true);
        $.ajax({
            url: basePath + "/api/announcement/delete.action",
            type: "POST",
            dataType: "json",
            data: { id: id },
            success: function (res) {
                if (res && res.success) {
                    $("tr[data-announcement-id='" + id + "']").fadeOut(300, function () {
                        $(this).remove();
                    });
                } else {
                    $button.prop("disabled", false);
                    alert((res && res.message) || "删除失败");
                }
            },
            error: function (xhr) {
                $button.prop("disabled", false);
                showAjaxError(xhr, "删除失败，请稍后重试");
            }
        });
    });

    $("#addUsername").on("blur", function () {
        var $input = $(this);
        var username = ($input.val() || "").trim();
        var $form = $input.closest("form");
        var $message = $form.find(".username-check-message");
        var $submit = $form.find(".add-user-submit");

        $input.removeClass("is-valid is-invalid");
        $message.removeClass("text-success text-danger").text("");
        $submit.prop("disabled", false);

        if (!username) {
            return;
        }

        $.ajax({
            url: basePath + "/api/user/checkUsername.action",
            type: "GET",
            dataType: "json",
            data: { username: username },
            success: function (res) {
                if (res && res.exists) {
                    $input.addClass("is-invalid");
                    $message.addClass("text-danger").text("用户名已存在，请更换");
                    $submit.prop("disabled", true);
                } else {
                    $input.addClass("is-valid");
                    $message.addClass("text-success").text("用户名可用");
                }
            },
            error: function () {
                $input.addClass("is-invalid");
                $message.addClass("text-danger").text("用户名检测失败，请稍后重试");
                $submit.prop("disabled", true);
            }
        });
    });

    $("#addUserModal").on("hidden.bs.modal", function () {
        var $modal = $(this);
        $modal.find("#addUsername").removeClass("is-valid is-invalid");
        $modal.find(".username-check-message").removeClass("text-success text-danger").text("");
        $modal.find(".add-user-submit").prop("disabled", false);
    });
});
