package com.sample.maps;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.joda.time.Duration;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;

public class ArrivalEstimator {
	
	protected static final String API_KEY;
	static {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("./src/com/sample/maps/google_cloud_server_api_key.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		API_KEY = prop.getProperty("api_key", "");
	}
	
	double srcLat = -1;
	double srcLon = -1;
	double dstLat = -1;
	double dstLon = -1;
	
	protected ArrivalEstimator() { this(-1, -1, -1, -1); }
	
	public ArrivalEstimator(double sLat, double sLon, double dLat, double dLon) {
		this.srcLat = sLat;
		this.srcLon = sLon;
		this.dstLat = dLat;
		this.dstLon = dLat;
	}

	public long estimate() {
//		String origin = "47.674067,-122.114471";
		String origin = "7977 170th AVE NE, Redmond, WA";
		String destination = "Bellevue Square, Bellevue, WA";
		try {
			GeoApiContext context = new GeoApiContext();
			context.setApiKey(API_KEY);
			context.setQueryRateLimit(2);
			DistanceMatrixApiRequest req =
					DistanceMatrixApi.getDistanceMatrix(context, new String[]{origin}, new String[]{destination});
			DistanceMatrix results = req.await();
			for(DistanceMatrixRow result : results.rows) {
				for(DistanceMatrixElement element : result.elements) {
					System.out.println(element.distance + ", " + element.duration + ", " + element.status);
					return element.duration.inSeconds;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1L;
	}
	
	public static void main(String ... argv) {
		ArrivalEstimator eta = new ArrivalEstimator();
		Long seconds = eta.estimate() * 1000L;
		System.out.println("Duration: " + seconds + "ms");
		Duration duration = new Duration(seconds);
		System.out.println(duration.getStandardHours() + "H " + duration.getStandardMinutes() + "M " + duration.getStandardSeconds() + "S");
		System.out.println(duration.toPeriod());
		return;
	}
}
