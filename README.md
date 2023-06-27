# Dive Log
This repository contains code for a dive log Android app that can read dives from
[hwOS](https://heinrichsweikamp.com/hw-os) family of dive computers (e.g. Heinrichs Weikamp OSTC Sport) via Bluetooth
LE (BLE).

Feature Plan:
- MVP:
  - Offline-first dive log
  - Fetch and merge dive logs from dive computer
  - Plot of dive profile
  - Additional data (equipment, observations, tags)
- Afterwards:
  - Synchronize dive logs in external cloud
  - Map with dive locations?
  - Header image?
  - Custom data fields (e.g. weights, tank, etc.)?


## Modules
- `app`: The application or presentation layer. Contains compose views, viewmodels, resources, etc.
- `data`: The data layer. Contains business logic components, e.g. for fetching data over BLE.


## Communication Concept
Bluetooth LE endpoints are called characteristics (e.g. "firmware revision" or "battery level") and are grouped into
services (e.g. "device information"). Characteristics and services are identified by UUIDs. Characteristics can allow:
- reading
- writing (with or without response, like TCP/UDP)
- notifications/indications (app gets notified by device if a value changed, with our without acknowledgement)

hwOS does not use Bluetooth LE characteristics directly as endpoints for fetching dives. Instead communication is done
through a **simulated virtual serial COM port** over four specific BLE characteristics. This is based on a
"Terminal I/O v2.0" protocol by Telit Wireless Solutions (formerly Stollmann E+V GmbH).

See [Terminal I/O Profile Client Implementation Guide](http://www.iot.com.tr/uploads/pdf/TIO_Implementation_Guide_r05.pdf).


## BLE API
- Service: Generic Access (`0x1800`)
    - Characteristic: Device Name (`0x2A00`, `READ`) &rarr; "OSTCs 21876"
    - Characteristic: Appearance (`0x2A01`, `READ`)
- Service: Generic Attribute (`0x1801`)
- Service: Device Information (`0x180A`)
    - Characteristic: PnP ID Name (`0x2A50`, `READ`)
- Service: Terminal IO (`0xFEFB`)
    - Characteristic: UART Data RX (`00000001-0000-1000-8000-008025000000`, `WRITE - NO RESPONSE`)
    - Characteristic: UART Data TX (`00000002-0000-1000-8000-008025000000`, `NOTIFY`)
    - Characteristic: UART Credits RX (`00000003-0000-1000-8000-008025000000`, `WRITE`)
    - Characteristic: UART Credits TX (`00000004-0000-1000-8000-008025000000`, `INDICATE`)


## Connection Workflow
1. App connects to (previously bonded) BLE device
2. App sets up indication to Credits TX
3. App sets up notification to Data TX
4. App sends credits (e.g. `0xFE` as UInt8 for 254 credits) via Credits RX
5. Device sends credits to app via Credits TX
6. App can now:
    - Send commands via Data RX
    - Receive responses via Data TX


## Package Format
Requests:
- Byte 0: Command ID
- Byte 1...n: Payload (variable length payloads like strings are `0x00` terminated)

Responses:
- Byte 0: Command ID (echo)
- Byte 1...(n-1): Payload
- Byte n: `0x4D`

See [hwOS API specification](https://code.heinrichsweikamp.com/public/hwos_code/raw-file/tip/doc/hwos_interface.pdf).


## Download Dives
First off we have to enable download mode by sending the "start communication" command (`0xBB`). Afterwards we can end
communication by sending the "exit communication" command (`0xFF`).

We have three options:
- Send "get compact headers" command (`0x6D`):
    - Device will always send all 256 dive headers (4096 bytes total)
    - Unused headers consist of `0xFF` and must be filtered
    - Includes date, divetime, max depth and dive number
- Send "get full headers" command (`0x61`):
    - 65536 bytes total
    - Includes more information than compact headers, like surface pressure, min. temperature, gas mix, deco model, etc.
- Send "get dive profile" command (`0x66`) with dive number (0-255).
    - Sends full header and dive profile
    - ~21 kB for a one-hour dive


## Things to keep in mind
- Track credits and make sure device always has enough.
- Always wait for echo and acknowledgement (`0x4D`) from device. This might require a UART request queue on top of the BLE request queue.
- Device exits COM mode after 120 seconds, so add a heartbeat or reconnect logic.


## Links
- [Mike's Android Bluetooth Guide](https://mike.cloud/android/2021/05/19/bluetooth.html)
- [hwOS source](https://code.heinrichsweikamp.com/public/hwos_code/file/tip)
- [Lovely rant about BLE serial communication](https://github.com/subsurface/subsurface/blob/master/core/qt-ble.cpp#L120-L136) by [@torvalds](https://github.com/torvalds)
