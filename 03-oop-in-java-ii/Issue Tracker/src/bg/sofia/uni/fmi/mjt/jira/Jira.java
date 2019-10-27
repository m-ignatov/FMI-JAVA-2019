package bg.sofia.uni.fmi.mjt.jira;

import bg.sofia.uni.fmi.mjt.jira.enums.IssueResolution;
import bg.sofia.uni.fmi.mjt.jira.enums.WorkAction;
import bg.sofia.uni.fmi.mjt.jira.interfaces.Filter;
import bg.sofia.uni.fmi.mjt.jira.interfaces.Repository;
import bg.sofia.uni.fmi.mjt.jira.issues.Issue;

public class Jira implements Filter, Repository {

    private static final int ISSUES_MAX_SIZE = 100;

    private int issuesSize;
    private Issue[] issues;

    public Jira() {
        issues = new Issue[ISSUES_MAX_SIZE];
        issuesSize = 0;
    }

    @Override
    public Issue find(String issueID) {
        for (int i = 0; i < issuesSize; i++) {
            if (issues[i].getIssueID().equals(issueID)) {
                return issues[i];
            }
        }
        return null;
    }

    @Override
    public void addIssue(Issue issue) {
        validate(issue);
        if (issuesSize == ISSUES_MAX_SIZE) {
            throw new UnsupportedOperationException("Issues size exceeded");
        }
        if (find(issue.getIssueID()) != null) {
            throw new UnsupportedOperationException(issue.getIssueID() + " already exists in JIRA");
        }
        issues[issuesSize++] = issue;
    }

    public void addActionToIssue(Issue issue, WorkAction action, String actionDescription) {
        validate(issue);
        issue.addAction(action, actionDescription);
    }

    public void resolveIssue(Issue issue, IssueResolution resolution) {
        validate(issue);
        issue.resolve(resolution);
    }

    private void validate(Issue issue) {
        if (issue == null) {
            throw new IllegalArgumentException("Issue cannot be null");
        }
        if (issue.getIssueID() == null) {
            throw new IllegalArgumentException("Invalid issue ID");
        }
    }
}
