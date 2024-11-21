package statistics;

import entities.Review;

import java.util.List;

public class AverageRatingAccumulator {
    private int totalRating = 0;
    private int reviewCount = 0;

    public void addReviews(List<Review> reviews) {
        for (Review review : reviews) {
            totalRating += review.getRating();
            reviewCount++;
        }
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
