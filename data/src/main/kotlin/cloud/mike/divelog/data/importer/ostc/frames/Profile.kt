package cloud.mike.divelog.data.importer.ostc.frames

internal data class Profile(
    val diveHeader: DiveHeaderFull,
    val profileHeader: ProfileHeader,
    val samples: List<ProfileSample>,
)

// <Full Header>
// <Profile Header>
// <Sample 1>
// <Sample 2>
// ...
// 0xFD 0xFD
internal fun ByteArray.parseProfile(): Profile {
    val fullHeader = this.parseFullHeader()
    val profileHeader = this
        .copyOfRange(DiveHeaderFull.SIZE_BYTES, this.size)
        .parseProfileHeader()
    val samples = this
        .copyOfRange(
            fromIndex = DiveHeaderFull.SIZE_BYTES + profileHeader.sizeBytes,
            toIndex = this.size - 2, // Without stop bytes
        )
        .parseProfileSamples()
    return Profile(
        diveHeader = fullHeader,
        profileHeader = profileHeader,
        samples = samples,
    )
}
