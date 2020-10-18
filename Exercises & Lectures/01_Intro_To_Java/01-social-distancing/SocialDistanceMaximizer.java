public class SocialDistanceMaximizer {

    public static int maxDistance(int[] seats) {
        int currentDistance = 0;
        int maxDistance = 0;
        boolean isLeftEnd = true;
        boolean isRightEnd = false;
        short spaceCounter = 0;

        for (int i = 0; i < seats.length; i++) {
            if (seats[i] == 0) {
                currentDistance++;
                if (currentDistance > maxDistance) {
                    maxDistance = currentDistance;

                    if (i == seats.length - 1) {
                        isRightEnd = true;
                    } else if (spaceCounter > 0) {
                        isLeftEnd = false;
                    }
                }
            } else {
                spaceCounter++;
                currentDistance = 0;
            }
        }
        if (isLeftEnd || isRightEnd) {
            return maxDistance;
        }
        return maxDistance % 2 == 0 ? maxDistance /= 2 : maxDistance / 2 + 1;
    }
}

