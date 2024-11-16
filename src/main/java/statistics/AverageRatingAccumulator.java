package statistics;

public class AverageRatingAccumulator {
    private int totalRating = 0;
    private int reviewCount = 0;

    void addRating(int rating) {
        totalRating += rating;
        reviewCount++;
    }

    AverageRatingAccumulator combine(AverageRatingAccumulator other) {
        this.totalRating += other.totalRating;
        this.reviewCount += other.reviewCount;
        return this;
    }

    double getAverage() {
        return reviewCount > 0 ? (double) totalRating / reviewCount : 0.0;
    }
}
