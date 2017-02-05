package com.calabozo.facedetector;

import java.util.ArrayList;
import java.util.List;

import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.FacialKeypoint;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.shape.Shape;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.Device;
import org.openimaj.video.capture.VideoCapture;
import org.openimaj.video.capture.VideoCaptureException;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws VideoCaptureException {
		List<Device> videoDevices = VideoCapture.getVideoDevices();
		System.out.println(videoDevices.size() + " video devices detected.");

		Video<MBFImage> video;
		video = new VideoCapture(640, 480, 24, videoDevices.get(0));

		VideoDisplay<MBFImage> display = VideoDisplay.createVideoDisplay(video);

		display.addVideoListener(new VideoDisplayListener<MBFImage>() {
			FaceDetector<KEDetectedFace, FImage> fd = new FKEFaceDetector();
			List<Iterable<Point2d>> facialKeyPoints = new ArrayList<Iterable<Point2d>>();
			List<Shape> facialShapes = new ArrayList<Shape>();

			int i = 1;

			public void beforeUpdate(MBFImage frame) {
				List<KEDetectedFace> faces = null;
				if (i % 10 == 0) {
					faces = fd.detectFaces(Transforms.calculateIntensity(frame));
					facialShapes = new ArrayList<Shape>();
					facialKeyPoints = new ArrayList<Iterable<Point2d>>();
					for (KEDetectedFace face : faces) {
						Shape shape = face.getShape();
						facialShapes.add(shape.clone());

						Point2d faceOrigin = new Point2dImpl(shape.minX(), shape.minY());

						FacialKeypoint[] keypoints = face.getKeypoints();
						ArrayList<Point2d> lpts = new ArrayList<Point2d>();
						for (FacialKeypoint point : keypoints) {
							point.position.translate(faceOrigin);
							lpts.add(point.position.clone());
						}
						facialKeyPoints.add(lpts);
					}
				}

				for (Shape shape : facialShapes) {
					frame.drawShape(shape, RGBColour.RED);
				}
				for (Iterable<Point2d> keyPoints : facialKeyPoints) {
					frame.drawPoints(keyPoints, RGBColour.WHITE, 10);
				}
				i++;
			}

			public void afterUpdate(VideoDisplay<MBFImage> display) {
			}
		});
	}
}
