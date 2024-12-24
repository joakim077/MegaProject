package com.example.bankapp;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // Custom metric 1: Total number of users
    public void trackTotalUsers(int totalUsers) {
        meterRegistry.gauge("total_users", totalUsers);
    }

    // Custom metric 2: Total number of active sessions
    public void trackActiveSessions(int activeSessions) {
        meterRegistry.gauge("active_sessions", activeSessions);
    }

    // Custom metric 3: Total number of failed login attempts
    public void trackFailedLogins(int failedLogins) {
        meterRegistry.counter("failed_logins", "status", "failed").increment();
    }

    // Custom metric 4: Total number of successful login attempts
    public void trackSuccessfulLogins(int successfulLogins) {
        meterRegistry.counter("successful_logins", "status", "successful").increment();
    }

    // Custom metric 5: API request count (e.g., for a specific endpoint)
    public void trackApiRequests(String endpoint) {
        meterRegistry.counter("api_requests", "endpoint", endpoint).increment();
    }

    // Custom metric 6: Response time for a specific API
    public void trackApiResponseTime(String endpoint, long responseTime) {
        meterRegistry.timer("api_response_time", "endpoint", endpoint).record(responseTime, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    // Custom metric 7: User registrations over time
    public void trackUserRegistrations(int registrations) {
        meterRegistry.counter("user_registrations", "action", "register").increment();
    }

    // Custom metric 8: User deletions over time
    public void trackUserDeletions(int deletions) {
        meterRegistry.counter("user_deletions", "action", "delete").increment();
    }

    // Custom metric 9: Disk space usage (in GB)
    public void trackDiskSpaceUsage(double diskSpaceUsage) {
        meterRegistry.gauge("disk_space_usage", diskSpaceUsage);
    }

    // Custom metric 10: Memory usage (in MB)
    public void trackMemoryUsage(double memoryUsage) {
        meterRegistry.gauge("memory_usage", memoryUsage);
    }

    // Custom metric 11: CPU usage percentage
    public void trackCpuUsage(double cpuUsage) {
        meterRegistry.gauge("cpu_usage", cpuUsage);
    }

    // Custom metric 12: Application restart count
    private int restartCount = 0;
    public void trackRestartCount() {
        meterRegistry.gauge("application_restart_count", restartCount);
    }
    
    public void incrementRestartCount() {
        restartCount++;
    }

    // Custom metric 13: Queue size for processing tasks
    public void trackQueueSize(int queueSize) {
        meterRegistry.gauge("queue_size", queueSize);
    }

    // Custom metric 14: Number of pending email notifications
    public void trackPendingEmailNotifications(int pendingEmails) {
        meterRegistry.gauge("pending_email_notifications", pendingEmails);
    }

    // Custom metric 15: System uptime (in seconds)
    public void trackSystemUptime(long uptimeInSeconds) {
        meterRegistry.gauge("system_uptime", uptimeInSeconds);
    }

    // Custom metric 16: External API response time
    public void trackExternalApiResponseTime(String apiName, long responseTime) {
        meterRegistry.timer("external_api_response_time", "api_name", apiName).record(responseTime, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    // Custom metric 17: Number of items processed in batch jobs
    public void trackBatchJobProcessing(int processedItems) {
        meterRegistry.counter("batch_job_processing", "job", "batch").increment();
    }

    // Custom metric 18: Number of scheduled tasks executed
    public void trackScheduledTasksExecuted(int tasksExecuted) {
        meterRegistry.counter("scheduled_tasks_executed", "task", "scheduled").increment();
    }

    // Custom metric 19: Number of database queries executed
    public void trackDatabaseQueries(int queryCount) {
        meterRegistry.counter("database_queries", "operation", "query").increment();
    }

    // Custom metric 20: Error rate for the application
    public void trackErrorRate(double errorRate) {
        meterRegistry.gauge("error_rate", errorRate);
    }
}
