/**
 * FitAura - JavaScript Client Library (Phase 1 Foundation)
 */
document.addEventListener('DOMContentLoaded', function () {
    console.log('FitAura Foundation initialized successfully.');

    // Optional dynamic health checker for foundation verification
    const healthBadge = document.getElementById('runtime-status-badge');
    if (healthBadge) {
        fetch('api/health')
            .then(res => res.json())
            .then(data => {
                if (data.status === 'UP') {
                    healthBadge.className = 'badge bg-success-subtle text-success border border-success-subtle';
                    healthBadge.textContent = 'Server Status: Active (Java ' + data.javaVersion + ')';
                }
            })
            .catch(() => {
                // If accessed directly without running servlet container
                healthBadge.className = 'badge bg-secondary-subtle text-secondary border border-secondary-subtle';
                healthBadge.textContent = 'Servlet Ready (Tomcat 10.1+)';
            });
    }
});
