package com.calabozo.facedetector;

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

			public void beforeUpdate(MBFImage frame) {
				List<KEDetectedFace> faces = fd.detectFaces(Transforms.calculateIntensity(frame));
				for (KEDetectedFace face : faces) {
					Shape shape = face.getShape();
					frame.drawShape(shape, RGBColour.RED);
					Point2d faceOrigin = new Point2dImpl(shape.minX(), shape.minY());
					FacialKeypoint[] keypoints = face.getKeypoints();
					for (FacialKeypoint point : keypoints) {
						point.position.translate(faceOrigin);
						frame.drawPoint(point.position, RGBColour.WHITE, 10);
					}
				}
			}

			public void afterUpdate(VideoDisplay<MBFImage> display) {
			}
		});
	}
}
