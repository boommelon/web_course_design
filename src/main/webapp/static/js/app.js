$(function () {
    var basePath = typeof contextPath === "string" ? contextPath : "";
    var fallbackBackdrop = null;

    initSidebar();
    initDeleteButtons();
    initUsernameCheck();
    initSelectionForm();
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
                "\u786e\u5b9a\u8981\u5220\u9664\u8be5\u7528\u6237\u5417\uff1f", "tr[data-user-id='{id}']");

        bindAjaxDelete(".ajax-delete-announcement", "/api/announcement/delete.action",
                "\u786e\u5b9a\u5220\u9664\u8be5\u516c\u544a\uff1f", "tr[data-announcement-id='{id}']");
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
                        alert((res && res.message) || "\u5220\u9664\u5931\u8d25");
                    }
                },
                error: function (xhr) {
                    $button.prop("disabled", false);
                    showAjaxError(xhr, "\u5220\u9664\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5");
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
                        setUsernameInvalid($input, $message, $submit, "\u7528\u6237\u540d\u5df2\u5b58\u5728\uff0c\u8bf7\u66f4\u6362");
                    } else {
                        $input.addClass("is-valid");
                        $message.addClass("text-success").text("\u7528\u6237\u540d\u53ef\u7528");
                    }
                },
                error: function () {
                    setUsernameInvalid($input, $message, $submit, "\u7528\u6237\u540d\u68c0\u6d4b\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5");
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

    function initSelectionForm() {
        $(".selection-form").on("submit", function () {
            var selected = [];
            var seen = {};
            var hasDuplicate = false;

            $(this).find("select[name^='choice']").each(function () {
                var value = $(this).val();
                $(this).removeClass("is-invalid");

                if (!value) {
                    return;
                }
                if (seen[value]) {
                    hasDuplicate = true;
                    $(this).addClass("is-invalid");
                } else {
                    seen[value] = true;
                    selected.push(value);
                }
            });

            if (selected.length === 0) {
                alert("\u8bf7\u81f3\u5c11\u9009\u62e9 1 \u4e2a\u5fd7\u613f\u9898\u76ee");
                return false;
            }
            if (hasDuplicate) {
                alert("\u5fd7\u613f\u9898\u76ee\u4e0d\u80fd\u91cd\u590d");
                return false;
            }
            return confirm("\u786e\u8ba4\u63d0\u4ea4\u672c\u8f6e\u5fd7\u613f\uff1f");
        });
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
