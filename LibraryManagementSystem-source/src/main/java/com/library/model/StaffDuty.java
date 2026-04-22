package com.library.model;

import java.time.LocalDateTime;

public class StaffDuty {
    private String        dutyId;
    private String        staffId;
    private String        assignedBy;
    private String        taskType;
    private String        description;
    private LocalDateTime assignedDate;
    private String        status;

    public StaffDuty(String dutyId, String staffId, String assignedBy,
                     String taskType, String description,
                     LocalDateTime assignedDate, String status) {
        this.dutyId       = dutyId;
        this.staffId      = staffId;
        this.assignedBy   = assignedBy;
        this.taskType     = taskType;
        this.description  = description;
        this.assignedDate = assignedDate;
        this.status       = status;
    }

    public String        getDutyId()       { return dutyId; }
    public String        getStaffId()      { return staffId; }
    public String        getAssignedBy()   { return assignedBy; }
    public String        getTaskType()     { return taskType; }
    public String        getDescription()  { return description; }
    public LocalDateTime getAssignedDate() { return assignedDate; }
    public String        getStatus()       { return status; }
    public void          setStatus(String s) { this.status = s; }
}