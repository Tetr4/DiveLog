package cloud.mike.divelog.data.importer.ostc.frames

internal data class DiveProfileFrame(
    val diveHeader: DiveHeaderFullFrame,
    val profileHeader: ProfileHeaderFrame,
    val samples: List<ProfileSampleFrame>,
)

// <Full Header>
// <Profile Header>
// <Sample 1>
// <Sample 2>
// ...
// 0xFD 0xFD
internal fun ByteArray.parseProfile(): DiveProfileFrame {
    val fullHeader = this.parseFullHeader()
    val profileHeader = this
        .copyOfRange(DiveHeaderFullFrame.SIZE_BYTES, this.size)
        .parseProfileHeader()
    val samples = this
        .copyOfRange(
            fromIndex = DiveHeaderFullFrame.SIZE_BYTES + profileHeader.sizeBytes,
            toIndex = this.size - 2, // Without stop bytes
        )
        .parseProfileSamples()
    return DiveProfileFrame(
        diveHeader = fullHeader,
        profileHeader = profileHeader,
        samples = samples,
    )
}
