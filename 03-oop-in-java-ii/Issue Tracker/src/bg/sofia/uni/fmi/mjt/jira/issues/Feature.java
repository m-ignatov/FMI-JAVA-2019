package bg.sofia.uni.fmi.mjt.jira.issues;

import bg.sofia.uni.fmi.mjt.jira.enums.IssuePriority;
import bg.sofia.uni.fmi.mjt.jira.enums.IssueResolution;
import bg.sofia.uni.fmi.mjt.jira.enums.IssueStatus;
import bg.sofia.uni.fmi.mjt.jira.enums.WorkAction;

import java.time.LocalDateTime;

public class Feature extends Issue {

    public Feature(IssuePriority priority, Component component, String description) {
        super(priority, component, description);
    }

    @Override
    public void resolve(IssueResolution resolution) {
        if (!(contains(WorkAction.DESIGN.name().toLowerCase())
                && contains(WorkAction.IMPLEMENTATION.name().toLowerCase())
                && contains(WorkAction.TESTS.name().toLowerCase()))) {
            throw new IllegalArgumentException("Features should have all mandatory action logs");
        }
        setStatus(IssueStatus.RESOLVED);
        setResolution(resolution);
        setLastModifiedOn(LocalDateTime.now());
    }

    private boolean contains(String workAction) {
        for (String action : getActionLog()) {
            if (action.contains(workAction)) {
                return true;
            }
        }
        return false;
    }
}
