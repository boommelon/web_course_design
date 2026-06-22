$(function () {
    var basePath = typeof contextPath === "string" ? contextPath : "";
    var fallbackBackdrop = null;

    initSidebar();
    initDeleteButtons();
    initUsernameCheck();
    initModalFallback();

    function initSidebar() {
        $(".sidebar a").each(function () {
            if (this.pathname === window.location.pathname) {
                $(this).addClass("active");
            }
        });
    }

    function initDeleteButtons() {
        bindAjaxDelete(".ajax-delete-user", "/api/user/delete.action",
                "确定要删除该用户吗？", "tr[data-user-id='{id}']");

        bindAjaxDelete(".ajax-delete-announcement", "/api/announcement/delete.action",
                "确定删除该公告？", "tr[data-announcement-id='{id}']");
    }

    function showAjaxError(xhr, fallbackMessage) {
        var message = fallbackMessage;
        if (xhr.responseJSON && xhr.responseJSON.message) {
            message = xhr.responseJSON.message;
        }
        alert(message);
    }

    function bindAjaxDelete(buttonSelector, url, confirmText, rowSelectorTemplate) {
        $(buttonSelector).on("click", function () {
            if (!confirm(confirmText)) {
                return;
            }

            var $button = $(this);
            var id = $button.data("id");

            $button.prop("disabled", true);
            $.ajax({
                url: basePath + url,
                type: "POST",
                dataType: "json",
                data: { id: id },
                success: function (res) {
                    if (res && res.success) {
                        removeTableRow(rowSelectorTemplate, id);
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
    }

    function removeTableRow(rowSelectorTemplate, id) {
        var rowSelector = rowSelectorTemplate.replace("{id}", id);
        $(rowSelector).fadeOut(300, function () {
            $(this).remove();
        });
    }

    function initUsernameCheck() {
        $("#addUsername").on("blur", function () {
            var $input = $(this);
            var username = ($input.val() || "").trim();
            var $form = $input.closest("form");
            var $message = $form.find(".username-check-message");
            var $submit = $form.find(".add-user-submit");

            resetUsernameCheck($input, $message, $submit);

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
                        setUsernameInvalid($input, $message, $submit, "用户名已存在，请更换");
                    } else {
                        $input.addClass("is-valid");
                        $message.addClass("text-success").text("用户名可用");
                    }
                },
                error: function () {
                    setUsernameInvalid($input, $message, $submit, "用户名检测失败，请稍后重试");
                }
            });
        });

        $("#addUserModal").on("hidden.bs.modal", function () {
            var $modal = $(this);
            resetUsernameCheck($modal.find("#addUsername"),
                    $modal.find(".username-check-message"),
                    $modal.find(".add-user-submit"));
        });
    }

    function resetUsernameCheck($input, $message, $submit) {
        $input.removeClass("is-valid is-invalid");
        $message.removeClass("text-success text-danger").text("");
        $submit.prop("disabled", false);
    }

    function setUsernameInvalid($input, $message, $submit, text) {
        $input.addClass("is-invalid");
        $message.addClass("text-danger").text(text);
        $submit.prop("disabled", true);
    }

    function initModalFallback() {
        $(document).on("click", '[data-bs-toggle="modal"][data-bs-target]', function (event) {
            event.preventDefault();
            event.stopPropagation();

            openModal($(this).attr("data-bs-target"));
        });

        $(document).on("click", '[data-bs-dismiss="modal"], .modal-backdrop', function () {
            if (!(window.bootstrap && window.bootstrap.Modal)) {
                closeFallbackModal();
            }
        });

        $(document).on("keydown", function (event) {
            if (event.key === "Escape" && !(window.bootstrap && window.bootstrap.Modal)) {
                closeFallbackModal();
            }
        });
    }

    function openModal(target) {
        var modal = document.querySelector(target);
        if (!modal) {
            return;
        }

        if (window.bootstrap && window.bootstrap.Modal) {
            window.bootstrap.Modal.getOrCreateInstance(modal).show();
        } else {
            openFallbackModal(target);
        }
    }

    function closeFallbackModal() {
        $(".modal.show").removeClass("show").hide().removeAttr("aria-modal");
        $("body").removeClass("modal-open").css("overflow", "");
        if (fallbackBackdrop) {
            fallbackBackdrop.remove();
            fallbackBackdrop = null;
        }
    }

    function openFallbackModal(target) {
        var $modal = $(target);
        if ($modal.length === 0) {
            return;
        }
        closeFallbackModal();
        fallbackBackdrop = $('<div class="modal-backdrop fade show"></div>').appendTo("body");
        $("body").addClass("modal-open").css("overflow", "hidden");
        $modal.addClass("show").show().attr("aria-modal", "true");
    }
});
