package org.jinzora.upnp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.Icon;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.support.connectionmanager.ConnectionManagerService;

public class MediaServer implements Runnable {
	public static final String TAG = "jinzora";
	
	public static void main(String[] args) throws Exception {
        // Start a user thread that runs the UPnP stack
		System.out.println("starting");
        Thread serverThread = new Thread(new MediaServer());
        serverThread.setDaemon(false);
        serverThread.start();
    }
	
	@SuppressWarnings("unchecked")
	protected LocalDevice createDevice() throws ValidationException,
			LocalServiceBindingException, IOException {

		DeviceType type = new UDADeviceType("MediaServer", 1);
		UpnpConfiguration config = new UpnpConfiguration();
		DeviceIdentity identity = config.getDeviceIdentity();
		DeviceDetails details = config.getDeviceDetails();

		Icon icon = new Icon("image/png", 48, 48, 8, getClass().getResource("/icon.png"));

		List<LocalService> localServices = new ArrayList<LocalService>();
		
			LocalService<CompatContentDirectory> jinzoraMediaService = new AnnotationLocalServiceBinder()
					.read(CompatContentDirectory.class);
			jinzoraMediaService.setManager(new DefaultServiceManager<CompatContentDirectory>(
					jinzoraMediaService, CompatContentDirectory.class));
			localServices.add(jinzoraMediaService);

		LocalService<ConnectionManagerService> connectionManagerService =
	        new AnnotationLocalServiceBinder().read(ConnectionManagerService.class);
		connectionManagerService.setManager(
		        new DefaultServiceManager<ConnectionManagerService>(
		        		connectionManagerService,
		                ConnectionManagerService.class
		        )
		);
		localServices.add(connectionManagerService);
		
		LocalService<MSMediaReceiverRegistrarService> receiverService =
			new AnnotationLocalServiceBinder().read(MSMediaReceiverRegistrarService.class);
		receiverService.setManager(new DefaultServiceManager<MSMediaReceiverRegistrarService>(
				receiverService, MSMediaReceiverRegistrarService.class));
		localServices.add(receiverService);
		
		return new LocalDevice(identity, type, details, icon, localServices.toArray(new LocalService[] {}));
	}
	
	public void run() {
        try {

            final UpnpService upnpService = new UpnpServiceImpl();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    upnpService.shutdown();
                }
            });

            // Add the bound local device to the registry
            upnpService.getRegistry().addDevice(
                    createDevice()
            );

        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
