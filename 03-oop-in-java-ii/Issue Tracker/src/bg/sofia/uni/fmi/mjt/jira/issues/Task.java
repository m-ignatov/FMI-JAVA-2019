package bg.sofia.uni.fmi.mjt.jira.issues;

import bg.sofia.uni.fmi.mjt.jira.enums.IssuePriority;
import bg.sofia.uni.fmi.mjt.jira.enums.IssueResolution;
import bg.sofia.uni.fmi.mjt.jira.enums.IssueStatus;
import bg.sofia.uni.fmi.mjt.jira.enums.WorkAction;

import java.time.LocalDateTime;

public class Task extends Issue {

    public Task(IssuePriority priority, Component component, String description) {
        super(priority, component, description);
    }

    @Override
    public void addAction(WorkAction action, String description) {
        if (action.equals(WorkAction.FIX)
                || action.equals(WorkAction.IMPLEMENTATION)
                || action.equals(WorkAction.TESTS)) {
            throw new UnsupportedOperationException("Task does not support " + action.name());
        }
        super.addAction(action, description);
    }

    @Override
    public void resolve(IssueResolution resolution) {
        setStatus(IssueStatus.RESOLVED);
        setResolution(resolution);
        setLastModifiedOn(LocalDateTime.now());
    }
}
