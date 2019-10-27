package bg.sofia.uni.fmi.mjt.jira.issues;

import bg.sofia.uni.fmi.mjt.jira.enums.IssuePriority;
import bg.sofia.uni.fmi.mjt.jira.enums.IssueResolution;
import bg.sofia.uni.fmi.mjt.jira.enums.IssueStatus;
import bg.sofia.uni.fmi.mjt.jira.enums.WorkAction;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Issue {

    private static final int ACTION_LOG_MAX_SIZE = 20;
    private static int issueCounter = 0;

    private String issueId;
    private String description;
    private IssuePriority priority;
    private IssueResolution resolution;
    private IssueStatus status;
    private Component component;
    private LocalDateTime createdOn;
    private LocalDateTime lastModifiedOn;
    private String[] actionLog;

    private int actionLogSize;

    public Issue(IssuePriority priority, Component component, String description) {
        this.priority = priority;
        this.component = component;
        this.description = description;

        this.actionLog = new String[ACTION_LOG_MAX_SIZE];
        this.actionLogSize = 0;

        this.issueId = String.format("%s-%d", component.getShortName(), issueCounter++);
        this.status = IssueStatus.OPEN;
        this.resolution = IssueResolution.UNRESOLVED;
        this.createdOn = LocalDateTime.now();
        this.lastModifiedOn = createdOn;
    }

    public String getIssueID() {
        return issueId;
    }

    public String getDescription() {
        return description;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public IssueResolution getResolution() {
        return resolution;
    }

    public void setResolution(IssueResolution resolution) {
        this.resolution = resolution;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
        setLastModifiedOn(LocalDateTime.now());
    }

    public Component getComponent() {
        return component;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public LocalDateTime getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(LocalDateTime lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public String[] getActionLog() {
        return actionLog;
    }

    public void addAction(WorkAction action, String description) {
        if (actionLogSize == ACTION_LOG_MAX_SIZE) {
            throw new UnsupportedOperationException("Action log size exceeded.");
        }
        if (description == null) {
            throw new IllegalArgumentException("Description is null");
        }
        if (action == null) {
            throw new IllegalArgumentException("Action is null");
        }
        actionLog[actionLogSize++] =
                String.format("%s: %s", action.name().toLowerCase(), description);
        setLastModifiedOn(LocalDateTime.now());
    }

    public abstract void resolve(IssueResolution resolution);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue issue = (Issue) o;
        return issueId.equals(issue.issueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issueId);
    }
}
