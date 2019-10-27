package bg.sofia.uni.fmi.mjt.jira.interfaces;

import bg.sofia.uni.fmi.mjt.jira.issues.Issue;

public interface Filter {

    // If there is no such Issue the method should return null
    Issue find(String issueID);
}
