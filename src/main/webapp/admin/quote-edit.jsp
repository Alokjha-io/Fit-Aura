<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Daily Quote – FitAura Admin</title>
    <!-- Bootstrap 5 CSS via CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fitaura.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">

    <nav class="navbar navbar-dark bg-dark py-2 border-bottom border-dark-subtle">
        <div class="container-fluid px-lg-5">
            <a class="navbar-brand d-flex align-items-center text-white" href="${pageContext.request.contextPath}/admin/quotes">
                <i class="bi bi-arrow-left me-2"></i> Back to Quotes
            </a>
        </div>
    </nav>

    <main class="container my-5 flex-grow-1" style="max-width: 680px;">
        <div class="card border rounded-4 shadow-sm bg-white p-4 p-md-5">
            <h4 class="fw-bold text-dark mb-1"><i class="bi bi-pencil-square text-success me-2"></i>Edit Motivational Quote</h4>
            <p class="text-secondary small mb-4">Modify the scheduled date, quote copy, or publication status.</p>

            <form action="${pageContext.request.contextPath}/admin/quotes" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                <input type="hidden" name="action" value="update"/>
                <input type="hidden" name="quoteId" value="${editQuote.quoteId}"/>

                <div class="mb-3">
                    <label class="form-label small fw-semibold">Quote Text *</label>
                    <textarea name="quoteText" rows="4" class="form-control rounded-3" required><c:out value="${editQuote.quoteText}"/></textarea>
                </div>

                <div class="mb-3">
                    <label class="form-label small fw-semibold">Author / Attribution</label>
                    <input type="text" name="authorName" class="form-control rounded-3" value="<c:out value="${editQuote.authorName}"/>" required/>
                </div>

                <div class="row g-3 mb-4">
                    <div class="col-md-6">
                        <label class="form-label small fw-semibold">Scheduled Date *</label>
                        <input type="date" name="quoteDate" class="form-control rounded-3" value="${editQuote.quoteDate}" required/>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label small fw-semibold">Status</label>
                        <select name="status" class="form-select rounded-3">
                            <option value="PUBLISHED" ${editQuote.status == 'PUBLISHED' ? 'selected' : ''}>PUBLISHED</option>
                            <option value="DRAFT" ${editQuote.status == 'DRAFT' ? 'selected' : ''}>DRAFT</option>
                            <option value="ARCHIVED" ${editQuote.status == 'ARCHIVED' ? 'selected' : ''}>ARCHIVED</option>
                        </select>
                    </div>
                </div>

                <div class="d-flex justify-content-between align-items-center pt-3 border-top">
                    <a href="${pageContext.request.contextPath}/admin/quotes" class="btn btn-outline-secondary btn-sm px-3">Cancel</a>
                    <button type="submit" class="btn btn-success btn-sm px-4 fw-semibold">Save Changes</button>
                </div>
            </form>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
