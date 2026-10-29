SUMMARY = "Control App from Wi-Fi Alliance QuickTrack Project"
DESCRIPTION = "control-app-open is a control agent for automating the Wi-Fi Alliance QuickTrack Project."
#HOMEPAGE = "http://wi-fi.org/"
SECTION = "qca"
# The RDKB Makefile assigns PKG_LICENSE twice: ISC first, then WFA.
# The fetched source README contains the ISC license text.
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://README.md;md5=a0c6de445730e0660409cffc3d28d522"

PV = "20230811"
PR = "r1.0.6"

SRC_URI = "git://github.com/Wi-FiQuickTrack/Wi-FiQuickTrack-ControlAppC.git;protocol=https;nobranch=1"
SRCREV = "a6bb580896cf8a985aba31cfc010a7fd11a9fc13"

S = "${WORKDIR}/git"

DEPENDS:append = " libbsd"

CFLAGS:remove = "-Werror"
CFLAGS:append = " -Wno-error -include bsd/string.h -U_FORTIFY_SOURCE -D_FORTIFY_SOURCE=0"
SECURITY_CFLAGS:remove = "-D_FORTIFY_SOURCE=2"

LDFLAGS:append = " -lbsd"

do_compile:prepend() {
      sed -i \
          -e 's#$(CC) $(CFLAGS) $(LDFLAGS) -o $@ $^#$(CC) $(CFLAGS) -o $@ $^ $(LDFLAGS)#' \
          -e 's#$(CC) $(CFLAGS) -o $@ $^#$(CC) $(CFLAGS) -o $@ $^ $(LDFLAGS)#' \
          ${S}/Makefile
}

do_compile() {
      oe_runmake -C "${S}" \
          CC="${CC}" \
          CFLAGS="${CFLAGS}" \
          LDFLAGS="${LDFLAGS}" \
          all
}


do_patch() {
    for p in $(ls ${THISDIR}/patches/*.patch | sort); do
        patch -d${S} -p1 --forward --fuzz=3 < $p || true
    done
}


do_install() {
    install -d ${D}${sbindir}
    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${sysconfdir}/config

    install -m 0755 ${S}/control_app ${D}${sbindir}/control_app
}

FILES:${PN} += "\
    ${sbindir}/control_app \
    ${sysconfdir}/init.d \
    ${sysconfdir}/config \
"
 
