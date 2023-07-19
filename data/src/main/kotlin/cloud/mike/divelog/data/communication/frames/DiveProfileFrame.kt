package cloud.mike.divelog.data.communication.frames

data class DiveProfileFrame(
    val diveHeader: FullHeaderFrame,
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
        .copyOfRange(fullHeader.sizeBytes, this.size)
        .parseProfileHeader()
    val startOfSamples = fullHeader.sizeBytes + profileHeader.sizeBytes
    val samples = this
        .copyOfRange(startOfSamples, profileHeader.profileDataLength - 2)
        .parseProfileSamples()
    return DiveProfileFrame(
        diveHeader = fullHeader,
        profileHeader = profileHeader,
        samples = samples,
    )
}
