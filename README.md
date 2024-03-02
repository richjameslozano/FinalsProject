TO DO:

NAVIGATION FUNCTIONS AND IMPLEMENT DIFFERENT DASHBOARDS PER USER


USER PROFILE AND MORE ACCOUNT SETTINGS LIKE CHANGE PASSWORD, DELETE ACCOUNT FOR BETTER USER EXPERIENCE


IMPLEMENT SUBCONTRACTOR MAPS ACTIVITY TO MAKE CUSTOMER MAPS ACTIVITY


TRANSACTION RECEIPT CONTAINING THE FF: PRODUCE UPON SUBCONTRACTOR DELIVERY STATUS UPDATE
Customer address
Subcontractor address
Employee Name
Subcontractor Name
Customer Name
Luggage Details
Delivery Date
Delivery Status (set by Subcontractor)
Delivery Amount (Compare longitude and latitude A and B using Haversine Formula)


double lat1 = 40.7128; // latitude of point 1 in degrees
        double lon1 = A1; // longitude of point A in degrees
        double lat2 = A2; // latitude of point A in degrees
        double lon2 = B1; // longitude of point B in degrees
        double lon2 = B2; // longitude of point B in degrees
        double distance = haversine(lat1, lon1, lat2, lon2);
        "Distance between the two points is: " + distance + " km"
public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0; // Earth radius in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
}
