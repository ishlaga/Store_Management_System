package marketing.config;

public class PromotionConfig {
    public static final String[] PROMOTION_TYPES = {
        "PERCENTAGE_OFF",
        "BOGO",
        "LOYALTY_POINTS_MULTIPLIER"
    };

    public static final String[] PROMOTION_STATUSES = {
        "ACTIVE",
        "COMPLETED",
        "SUSPENDED"
    };

    public static boolean isValidPromotionType(String type) {
        for (String validType : PROMOTION_TYPES) {
            if (validType.equals(type)) return true;
        }
        return false;
    }

    public static boolean isValidStatus(String status) {
        for (String validStatus : PROMOTION_STATUSES) {
            if (validStatus.equals(status)) return true;
        }
        return false;
    }
}