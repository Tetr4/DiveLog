# Shearwater Bluetooth Import
[Shearwater](https://www.shearwater.com/) Petrel and Perdix computers use the "Petrel Native Format" for bluetooth
communication. This format is not public, so this is based on code in
[libdivecomputer](https://github.com/libdivecomputer/libdivecomputer/blob/master/src/shearwater_petrel.c).


## Bluetooth LE
Shearwater devices use a single serial port characteristic (UUID: `27b7570b-359e-45a3-91bb-cf7e70049bd2`) for receiving
(NOTIFY) and sending (WRITE) messages.

## Download
We can send commands over the serial port characteristic to download **raw chunks of memory**:
- `0x34`: Start download with given memory address and size.
- `0x35`: Enabled or disable compression
- `0x36`: Fetch the next block of current download
- `0x37`: End download

There are two locations of interest:
- `0xE0000000`: Manifest pages
- `0x80000000`: Dive data

See [ShearwaterConnection.kt](../data/src/main/kotlin/cloud/mike/divelog/data/importer/shearwater/ShearwaterConnection.kt)
for implementation details.
