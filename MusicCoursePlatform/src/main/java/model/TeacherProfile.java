package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * TeacherProfile entity class representing a teacher's profile in the Music Course Platform.
 * Contains detailed information about a teacher including biography, instruments, experience, etc.
 * 
 * @author Lu Liu
 * @version 1.0 (Sprint 3)
 */
public class TeacherProfile {

    private int profileId;
    private int userId;
    private String biography;
    private String instrumentsTaught;  // Comma-separated list of instruments
    private int yearsExperience;
    private BigDecimal hourlyRate;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Optional: Associated User object for convenience
    private User user;

    /**
     * Default constructor
     */
    public TeacherProfile() {
    }

    /**
     * Constructor with essential fields
     * 
     * @param userId The ID of the associated user
     * @param biography Teacher's biography
     * @param instrumentsTaught Comma-separated list of instruments
     * @param yearsExperience Years of teaching experience
     * @param hourlyRate Hourly rate for lessons
     * @param location Teacher's location
     */
    public TeacherProfile(int userId, String biography, String instrumentsTaught,
                          int yearsExperience, BigDecimal hourlyRate, String location) {
        this.userId = userId;
        this.biography = biography;
        this.instrumentsTaught = instrumentsTaught;
        this.yearsExperience = yearsExperience;
        this.hourlyRate = hourlyRate;
        this.location = location;
    }

    /**
     * Full constructor with all fields
     */
    public TeacherProfile(int profileId, int userId, String biography, String instrumentsTaught,
                          int yearsExperience, BigDecimal hourlyRate, String location,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.profileId = profileId;
        this.userId = userId;
        this.biography = biography;
        this.instrumentsTaught = instrumentsTaught;
        this.yearsExperience = yearsExperience;
        this.hourlyRate = hourlyRate;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ==================== Getters and Setters ====================

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getInstrumentsTaught() {
        return instrumentsTaught;
    }

    public void setInstrumentsTaught(String instrumentsTaught) {
        this.instrumentsTaught = instrumentsTaught;
    }

    public int getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(int yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // ==================== Utility Methods ====================

    /**
     * Get instruments as a list
     * @return List of instruments taught
     */
    public List<String> getInstrumentsAsList() {
        if (instrumentsTaught == null || instrumentsTaught.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(instrumentsTaught.split(","));
    }

    /**
     * Set instruments from a list
     * @param instruments List of instruments to set
     */
    public void setInstrumentsFromList(List<String> instruments) {
        if (instruments == null || instruments.isEmpty()) {
            this.instrumentsTaught = "";
        } else {
            this.instrumentsTaught = String.join(",", instruments);
        }
    }

    /**
     * Check if teacher teaches a specific instrument
     * @param instrument The instrument to check
     * @return true if teacher teaches this instrument
     */
    public boolean teachesInstrument(String instrument) {
        if (instrumentsTaught == null || instrument == null) {
            return false;
        }
        return getInstrumentsAsList().stream()
                .anyMatch(i -> i.trim().equalsIgnoreCase(instrument.trim()));
    }

    @Override
    public String toString() {
        return "TeacherProfile{" +
                "profileId=" + profileId +
                ", userId=" + userId +
                ", instrumentsTaught='" + instrumentsTaught + '\'' +
                ", yearsExperience=" + yearsExperience +
                ", hourlyRate=" + hourlyRate +
                ", location='" + location + '\'' +
                '}';
    }
}
