# AlienFX for Alienware 13 R2
This is a reverse-engineering project to control the lights on a Dell Alienware 13 R2 laptop.
This model has different light zones than the ones already configured previously.

## References
The following projects were used as a reference for building this one:
- [trackmastersteve/alienfx](https://github.com/trackmastersteve/alienfx)
- [snooze6/hack-alienfx](https://github.com/snooze6/hack-alienfx)

## Dependencies
- Java 25
- [Java Does USB](https://github.com/manuelbl/JavaDoesUSB)
- Dell Alienware 13 R2 Laptop
- USB Device (VID: 0x187C, PID: 0x0527) (Alienware Corporation AW13)

## Caveats
This project was only tested on a Dell Alienware 13 R2 running Arch Linux.
Other operating systems may have different commands or permissions.

## System Configuration Instructions

### Configuring UDEV Permissions
The program needs access to a USB device which normally is not allowed.
We can enable access using a UDEV rule. Please create the necessary files.

#### /etc/udev/rules.d/99-alienware-usb-permissions.rules
```
SUBSYSTEM=="usb", ATTR{idVendor}=="187c", ATTR{idProduct}=="0527", MODE="0666"
```

### Reloading UDEV Rules
Once you have created that file, please reload the UDEV rules with the following commands.
```shell
sudo udevadm control --reload
sudo udevadm trigger
```