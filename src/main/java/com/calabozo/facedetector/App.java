package com.calabozo.facedetector;

import java.util.List;

import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
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
			FaceDetector<DetectedFace, FImage> fd = new HaarCascadeDetector(100);
			public void beforeUpdate(MBFImage frame) {
				List<DetectedFace> faces = fd.detectFaces(Transforms.calculateIntensity(frame));
				for (DetectedFace face : faces)
					frame.drawShape(face.getBounds(), RGBColour.RED);
			}
			public void afterUpdate(VideoDisplay<MBFImage> display) {}
		});
	}
}
