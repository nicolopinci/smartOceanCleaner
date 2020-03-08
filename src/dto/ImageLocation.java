package dto;

import beans.Location;
import beans.Image;

public class ImageLocation {
	
	Image image;
	Location location;
	
	public ImageLocation() {
		super();
	}
	
	public Image getImage() {
		return image;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
}
