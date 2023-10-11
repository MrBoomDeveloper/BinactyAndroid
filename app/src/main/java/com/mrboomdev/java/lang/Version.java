package com.mrboomdev.java.lang;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Version implements Comparable<Version> {
	private final List<Integer> version;
	private final Optional<String> pre;
	private final Optional<Integer> build;
	private final Optional<String> optional;

	public Version(List<Integer> unmodifiableListOfVersions, Optional<String> pre, Optional<Integer> build, Optional<String> optional) {
		this.version = unmodifiableListOfVersions;
		this.pre = pre;
		this.build = build;
		this.optional = optional;
	}

	public static Version parse(String s) {
		if(s == null) {
			throw new NullPointerException();
		} else if(isSimpleNumber(s)) {
			return new Version(List.of(Integer.parseInt(s)), Optional.empty(), Optional.empty(), Optional.empty());
		} else {
			Matcher m = VersionPattern.VSTR_PATTERN.matcher(s);
			if(!m.matches()) {
				throw new IllegalArgumentException("Invalid version string: '" + s + "'");
			} else {
				String[] split = m.group("VNUM").split("\\.");
				Integer[] version = new Integer[split.length];

				for(int i = 0; i < split.length; ++i) {
					version[i] = Integer.parseInt(split[i]);
				}

				Optional<String> pre = Optional.ofNullable(m.group("PRE"));
				String b = m.group("BUILD");
				Optional<Integer> build = b == null ? Optional.empty() : Optional.of(Integer.parseInt(b));
				Optional<String> optional = Optional.ofNullable(m.group("OPT"));
				if(!build.isPresent()) {
					if(m.group("PLUS") != null) {
						if(!optional.isPresent()) {
							throw new IllegalArgumentException("'+' found with neither build or optional components: '" + s + "'");
						}

						if(pre.isPresent()) {
							throw new IllegalArgumentException("'+' found with pre-release and optional components:'" + s + "'");
						}
					} else if(optional.isPresent() && !pre.isPresent()) {
						throw new IllegalArgumentException("optional component must be preceded by a pre-release component or '+': '" + s + "'");
					}
				}

				return new Version(List.of(version), pre, build, optional);
			}
		}
	}

	private static boolean isSimpleNumber(String s) {
		for(int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			int lowerBound = i > 0 ? 48 : 49;
			if (c < lowerBound || c > '9') {
				return false;
			}
		}

		return true;
	}

	public int feature() {
		return (Integer)this.version.get(0);
	}

	public int interim() {
		return this.version.size() > 1 ? (Integer)this.version.get(1) : 0;
	}

	public int update() {
		return this.version.size() > 2 ? (Integer)this.version.get(2) : 0;
	}

	public int patch() {
		return this.version.size() > 3 ? (Integer)this.version.get(3) : 0;
	}

	/** @deprecated */
	@Deprecated(
			since = "10"
	)
	public int major() {
		return this.feature();
	}

	/** @deprecated */
	@Deprecated(
			since = "10"
	)
	public int minor() {
		return this.interim();
	}

	/** @deprecated */
	@Deprecated(
			since = "10"
	)
	public int security() {
		return this.update();
	}

	public List<Integer> version() {
		return this.version;
	}

	public Optional<String> pre() {
		return this.pre;
	}

	public Optional<Integer> build() {
		return this.build;
	}

	public Optional<String> optional() {
		return this.optional;
	}

	public int compareTo(Version obj) {
		return this.compare(obj, false);
	}

	public int compareToIgnoreOptional(Version obj) {
		return this.compare(obj, true);
	}

	private int compare(Version obj, boolean ignoreOpt) {
		if (obj == null) {
			throw new NullPointerException();
		} else {
			int ret = this.compareVersion(obj);
			if (ret != 0) {
				return ret;
			} else {
				ret = this.comparePre(obj);
				if (ret != 0) {
					return ret;
				} else {
					ret = this.compareBuild(obj);
					if (ret != 0) {
						return ret;
					} else {
						return !ignoreOpt ? this.compareOptional(obj) : 0;
					}
				}
			}
		}
	}

	private int compareVersion(Version obj) {
		int size = this.version.size();
		int oSize = obj.version().size();
		int min = Math.min(size, oSize);

		for(int i = 0; i < min; ++i) {
			int val = (Integer)this.version.get(i);
			int oVal = (Integer)obj.version().get(i);
			if (val != oVal) {
				return val - oVal;
			}
		}

		return size - oSize;
	}

	private int comparePre(Version obj) {
		Optional<String> oPre = obj.pre();
		if (!this.pre.isPresent()) {
			return oPre.isPresent() ? 1 : 0;
		} else if (!oPre.isPresent()) {
			return -1;
		} else {
			String val = (String)this.pre.get();
			String oVal = (String)oPre.get();
			if (val.matches("\\d+")) {
				return oVal.matches("\\d+") ? (new BigInteger(val)).compareTo(new BigInteger(oVal)) : -1;
			} else {
				return oVal.matches("\\d+") ? 1 : val.compareTo(oVal);
			}
		}
	}

	private int compareBuild(Version obj) {
		Optional<Integer> oBuild = obj.build();
		if (oBuild.isPresent()) {
			return this.build.isPresent() ? ((Integer)this.build.get()).compareTo((Integer)oBuild.get()) : -1;
		} else {
			return this.build.isPresent() ? 1 : 0;
		}
	}

	private int compareOptional(Version obj) {
		Optional<String> oOpt = obj.optional();
		if (!this.optional.isPresent()) {
			return oOpt.isPresent() ? -1 : 0;
		} else {
			return !oOpt.isPresent() ? 1 : ((String)this.optional.get()).compareTo((String)oOpt.get());
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder((String)this.version.stream().map(Object::toString).collect(Collectors.joining(".")));
		this.pre.ifPresent((v) -> {
			sb.append("-").append(v);
		});
		if (this.build.isPresent()) {
			sb.append("+").append(this.build.get());
			if (this.optional.isPresent()) {
				sb.append("-").append((String)this.optional.get());
			}
		} else if (this.optional.isPresent()) {
			sb.append(this.pre.isPresent() ? "-" : "+-");
			sb.append((String)this.optional.get());
		}

		return sb.toString();
	}

	public boolean equals(Object obj) {
		boolean ret = this.equalsIgnoreOptional(obj);
		if (!ret) {
			return false;
		} else {
			Version that = (Version)obj;
			return this.optional().equals(that.optional());
		}
	}

	public boolean equalsIgnoreOptional(Object obj) {
		if (this == obj) {
			return true;
		} else {
			boolean var10000;
			if (obj instanceof Version) {
				Version that = (Version)obj;
				if (this.version().equals(that.version()) && this.pre().equals(that.pre()) && this.build().equals(that.build())) {
					var10000 = true;
					return var10000;
				}
			}

			var10000 = false;
			return var10000;
		}
	}

	public int hashCode() {
		int h = 1;
		int p = 17;
		h = p * h + this.version.hashCode();
		h = p * h + this.pre.hashCode();
		h = p * h + this.build.hashCode();
		h = p * h + this.optional.hashCode();
		return h;
	}

	private static class VersionPattern {
		private static final String VNUM = "(?<VNUM>[1-9][0-9]*(?:(?:\\.0)*\\.[1-9][0-9]*)*)";
		private static final String PRE = "(?:-(?<PRE>[a-zA-Z0-9]+))?";
		private static final String BUILD = "(?:(?<PLUS>\\+)(?<BUILD>0|[1-9][0-9]*)?)?";
		private static final String OPT = "(?:-(?<OPT>[-a-zA-Z0-9.]+))?";
		private static final String VSTR_FORMAT = "(?<VNUM>[1-9][0-9]*(?:(?:\\.0)*\\.[1-9][0-9]*)*)(?:-(?<PRE>[a-zA-Z0-9]+))?(?:(?<PLUS>\\+)(?<BUILD>0|[1-9][0-9]*)?)?(?:-(?<OPT>[-a-zA-Z0-9.]+))?";
		static final Pattern VSTR_PATTERN = Pattern.compile("(?<VNUM>[1-9][0-9]*(?:(?:\\.0)*\\.[1-9][0-9]*)*)(?:-(?<PRE>[a-zA-Z0-9]+))?(?:(?<PLUS>\\+)(?<BUILD>0|[1-9][0-9]*)?)?(?:-(?<OPT>[-a-zA-Z0-9.]+))?");
		static final String VNUM_GROUP = "VNUM";
		static final String PRE_GROUP = "PRE";
		static final String PLUS_GROUP = "PLUS";
		static final String BUILD_GROUP = "BUILD";
		static final String OPT_GROUP = "OPT";

		private VersionPattern() {
		}
	}
}