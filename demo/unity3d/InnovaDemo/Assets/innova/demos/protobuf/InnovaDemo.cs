// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InnovaDemo.proto
#pragma warning disable 1591, 0612, 3021
#region Designer generated code

using pb = global::Google.Protobuf;
using pbc = global::Google.Protobuf.Collections;
using pbr = global::Google.Protobuf.Reflection;
using scg = global::System.Collections.Generic;
namespace Innova.Demo {

  /// <summary>Holder for reflection information generated from InnovaDemo.proto</summary>
  public static partial class InnovaDemoReflection {

    #region Descriptor
    /// <summary>File descriptor for InnovaDemo.proto</summary>
    public static pbr::FileDescriptor Descriptor {
      get { return descriptor; }
    }
    private static pbr::FileDescriptor descriptor;

    static InnovaDemoReflection() {
      byte[] descriptorData = global::System.Convert.FromBase64String(
          string.Concat(
            "ChBJbm5vdmFEZW1vLnByb3RvEgtpbm5vdmEuZGVtbxoTaW5ub3ZhX2NvbW1v",
            "bi5wcm90bxoVaW5ub3ZhX3Byb3RvY29sLnByb3RvIj4KCkFjdG9yQmFzaWMS",
            "EgoKYXBwZWFyYW5jZRgBIAEoCRIMCgRuYW1lGAIgASgJEg4KBnBhcmVudBgD",
            "IAEoCSK2AQoNQWN0b3JNb3ZlbWVudBInCghsb2NhdGlvbhgBIAEoCzIVLmlu",
            "bm92YS5jb21tb24uRmxvYXQzEicKCHZlbG9jaXR5GAIgASgLMhUuaW5ub3Zh",
            "LmNvbW1vbi5GbG9hdDMSKwoIcm90YXRpb24YAyABKAsyGS5pbm5vdmEuY29t",
            "bW9uLlF1YXRlcm5pb24SJgoHYW5ndWxhchgEIAEoCzIVLmlubm92YS5jb21t",
            "b24uRmxvYXQ0IjIKCkFjdG9yU2NhbGUSJAoFc2NhbGUYASABKAsyFS5pbm5v",
            "dmEuY29tbW9uLkZsb2F0MyJICg5Db25maWdHcmFwaGljcxIbChNtYXhfY2Ft",
            "ZXJhX2Rpc3RhbmNlGAEgASgCEhkKEW1heF92aWV3X2Rpc3RhbmNlGAIgASgC",
            "KjoKBVBUWVBFEgkKBUJBU0lDEAASDAoITU9WRU1FTlQQARIJCgVTQ0FMRRAC",
            "Eg0KCU5VTV9QVFlQRRADKiQKBUNUWVBFEgwKCEdSQVBISUNTEAASDQoJTlVN",
            "X0NUWVBFEAFiBnByb3RvMw=="));
      descriptor = pbr::FileDescriptor.FromGeneratedCode(descriptorData,
          new pbr::FileDescriptor[] { global::Innova.Common.InnovaCommonReflection.Descriptor, global::Innova.Protocol.InnovaProtocolReflection.Descriptor, },
          new pbr::GeneratedClrTypeInfo(new[] {typeof(global::Innova.Demo.PTYPE), typeof(global::Innova.Demo.CTYPE), }, new pbr::GeneratedClrTypeInfo[] {
            new pbr::GeneratedClrTypeInfo(typeof(global::Innova.Demo.ActorBasic), global::Innova.Demo.ActorBasic.Parser, new[]{ "Appearance", "Name", "Parent" }, null, null, null),
            new pbr::GeneratedClrTypeInfo(typeof(global::Innova.Demo.ActorMovement), global::Innova.Demo.ActorMovement.Parser, new[]{ "Location", "Velocity", "Rotation", "Angular" }, null, null, null),
            new pbr::GeneratedClrTypeInfo(typeof(global::Innova.Demo.ActorScale), global::Innova.Demo.ActorScale.Parser, new[]{ "Scale" }, null, null, null),
            new pbr::GeneratedClrTypeInfo(typeof(global::Innova.Demo.ConfigGraphics), global::Innova.Demo.ConfigGraphics.Parser, new[]{ "MaxCameraDistance", "MaxViewDistance" }, null, null, null)
          }));
    }
    #endregion

  }
  #region Enums
  public enum PTYPE {
    [pbr::OriginalName("BASIC")] Basic = 0,
    [pbr::OriginalName("MOVEMENT")] Movement = 1,
    [pbr::OriginalName("SCALE")] Scale = 2,
    [pbr::OriginalName("NUM_PTYPE")] NumPtype = 3,
  }

  public enum CTYPE {
    [pbr::OriginalName("GRAPHICS")] Graphics = 0,
    [pbr::OriginalName("NUM_CTYPE")] NumCtype = 1,
  }

  #endregion

  #region Messages
  public sealed partial class ActorBasic : pb::IMessage<ActorBasic> {
    private static readonly pb::MessageParser<ActorBasic> _parser = new pb::MessageParser<ActorBasic>(() => new ActorBasic());
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public static pb::MessageParser<ActorBasic> Parser { get { return _parser; } }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public static pbr::MessageDescriptor Descriptor {
      get { return global::Innova.Demo.InnovaDemoReflection.Descriptor.MessageTypes[0]; }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    pbr::MessageDescriptor pb::IMessage.Descriptor {
      get { return Descriptor; }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public ActorBasic() {
      OnConstruction();
    }

    partial void OnConstruction();

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public ActorBasic(ActorBasic other) : this() {
      appearance_ = other.appearance_;
      name_ = other.name_;
      parent_ = other.parent_;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public ActorBasic Clone() {
      return new ActorBasic(this);
    }

    /// <summary>Field number for the "appearance" field.</summary>
    public const int AppearanceFieldNumber = 1;
    private string appearance_ = "";
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public string Appearance {
      get { return appearance_; }
      set {
        appearance_ = pb::ProtoPreconditions.CheckNotNull(value, "value");
      }
    }

    /// <summary>Field number for the "name" field.</summary>
    public const int NameFieldNumber = 2;
    private string name_ = "";
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public string Name {
      get { return name_; }
      set {
        name_ = pb::ProtoPreconditions.CheckNotNull(value, "value");
      }
    }

    /// <summary>Field number for the "parent" field.</summary>
    public const int ParentFieldNumber = 3;
    private string parent_ = "";
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public string Parent {
      get { return parent_; }
      set {
        parent_ = pb::ProtoPreconditions.CheckNotNull(value, "value");
      }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public override bool Equals(object other) {
      return Equals(other as ActorBasic);
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public bool Equals(ActorBasic other) {
      if (ReferenceEquals(other, null)) {
        return false;
      }
      if (ReferenceEquals(other, this)) {
        return true;
      }
      if (Appearance != other.Appearance) return false;
      if (Name != other.Name) return false;
      if (Parent != other.Parent) return false;
      return true;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public override int GetHashCode() {
      int hash = 1;
      if (Appearance.Length != 0) hash ^= Appearance.GetHashCode();
      if (Name.Length != 0) hash ^= Name.GetHashCode();
      if (Parent.Length != 0) hash ^= Parent.GetHashCode();
      return hash;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public override string ToString() {
      return pb::JsonFormatter.ToDiagnosticString(this);
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public void WriteTo(pb::CodedOutputStream output) {
      if (Appearance.Length != 0) {
        output.WriteRawTag(10);
        output.WriteString(Appearance);
      }
      if (Name.Length != 0) {
        output.WriteRawTag(18);
        output.WriteString(Name);
      }
      if (Parent.Length != 0) {
        output.WriteRawTag(26);
        output.WriteString(Parent);
      }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public int CalculateSize() {
      int size = 0;
      if (Appearance.Length != 0) {
        size += 1 + pb::CodedOutputStream.ComputeStringSize(Appearance);
      }
      if (Name.Length != 0) {
        size += 1 + pb::CodedOutputStream.ComputeStringSize(Name);
      }
      if (Parent.Length != 0) {
        size += 1 + pb::CodedOutputStream.ComputeStringSize(Parent);
      }
      return size;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public void MergeFrom(ActorBasic other) {
      if (other == null) {
        return;
      }
      if (other.Appearance.Length != 0) {
        Appearance = other.Appearance;
      }
      if (other.Name.Length != 0) {
        Name = other.Name;
      }
      if (other.Parent.Length != 0) {
        Parent = other.Parent;
      }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public void MergeFrom(pb::CodedInputStream input) {
      uint tag;
      while ((tag = input.ReadTag()) != 0) {
        switch(tag) {
          default:
            input.SkipLastField();
            break;
          case 10: {
            Appearance = input.ReadString();
            break;
          }
          case 18: {
            Name = input.ReadString();
            break;
          }
          case 26: {
            Parent = input.ReadString();
            break;
          }
        }
      }
    }

  }

  public sealed partial class ActorMovement : pb::IMessage<ActorMovement> {
    private static readonly pb::MessageParser<ActorMovement> _parser = new pb::MessageParser<ActorMovement>(() => new ActorMovement());
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public static pb::MessageParser<ActorMovement> Parser { get { return _parser; } }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public static pbr::MessageDescriptor Descriptor {
      get { return global::Innova.Demo.InnovaDemoReflection.Descriptor.MessageTypes[1]; }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    pbr::MessageDescriptor pb::IMessage.Descriptor {
      get { return Descriptor; }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public ActorMovement() {
      OnConstruction();
    }

    partial void OnConstruction();

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public ActorMovement(ActorMovement other) : this() {
      Location = other.location_ != null ? other.Location.Clone() : null;
      Velocity = other.velocity_ != null ? other.Velocity.Clone() : null;
      Rotation = other.rotation_ != null ? other.Rotation.Clone() : null;
      Angular = other.angular_ != null ? other.Angular.Clone() : null;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public ActorMovement Clone() {
      return new ActorMovement(this);
    }

    /// <summary>Field number for the "location" field.</summary>
    public const int LocationFieldNumber = 1;
    private global::Innova.Common.Float3 location_;
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public global::Innova.Common.Float3 Location {
      get { return location_; }
      set {
        location_ = value;
      }
    }

    /// <summary>Field number for the "velocity" field.</summary>
    public const int VelocityFieldNumber = 2;
    private global::Innova.Common.Float3 velocity_;
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public global::Innova.Common.Float3 Velocity {
      get { return velocity_; }
      set {
        velocity_ = value;
      }
    }

    /// <summary>Field number for the "rotation" field.</summary>
    public const int RotationFieldNumber = 3;
    private global::Innova.Common.Quaternion rotation_;
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public global::Innova.Common.Quaternion Rotation {
      get { return rotation_; }
      set {
        rotation_ = value;
      }
    }

    /// <summary>Field number for the "angular" field.</summary>
    public const int AngularFieldNumber = 4;
    private global::Innova.Common.Float4 angular_;
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public global::Innova.Common.Float4 Angular {
      get { return angular_; }
      set {
        angular_ = value;
      }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public override bool Equals(object other) {
      return Equals(other as ActorMovement);
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public bool Equals(ActorMovement other) {
      if (ReferenceEquals(other, null)) {
        return false;
      }
      if (ReferenceEquals(other, this)) {
        return true;
      }
      if (!object.Equals(Location, other.Location)) return false;
      if (!object.Equals(Velocity, other.Velocity)) return false;
      if (!object.Equals(Rotation, other.Rotation)) return false;
      if (!object.Equals(Angular, other.Angular)) return false;
      return true;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public override int GetHashCode() {
      int hash = 1;
      if (location_ != null) hash ^= Location.GetHashCode();
      if (velocity_ != null) hash ^= Velocity.GetHashCode();
      if (rotation_ != null) hash ^= Rotation.GetHashCode();
      if (angular_ != null) hash ^= Angular.GetHashCode();
      return hash;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public override string ToString() {
      return pb::JsonFormatter.ToDiagnosticString(this);
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public void WriteTo(pb::CodedOutputStream output) {
      if (location_ != null) {
        output.WriteRawTag(10);
        output.WriteMessage(Location);
      }
      if (velocity_ != null) {
        output.WriteRawTag(18);
        output.WriteMessage(Velocity);
      }
      if (rotation_ != null) {
        output.WriteRawTag(26);
        output.WriteMessage(Rotation);
      }
      if (angular_ != null) {
        output.WriteRawTag(34);
        output.WriteMessage(Angular);
      }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public int CalculateSize() {
      int size = 0;
      if (location_ != null) {
        size += 1 + pb::CodedOutputStream.ComputeMessageSize(Location);
      }
      if (velocity_ != null) {
        size += 1 + pb::CodedOutputStream.ComputeMessageSize(Velocity);
      }
      if (rotation_ != null) {
        size += 1 + pb::CodedOutputStream.ComputeMessageSize(Rotation);
      }
      if (angular_ != null) {
        size += 1 + pb::CodedOutputStream.ComputeMessageSize(Angular);
      }
      return size;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public void MergeFrom(ActorMovement other) {
      if (other == null) {
        return;
      }
      if (other.location_ != null) {
        if (location_ == null) {
          location_ = new global::Innova.Common.Float3();
        }
        Location.MergeFrom(other.Location);
      }
      if (other.velocity_ != null) {
        if (velocity_ == null) {
          velocity_ = new global::Innova.Common.Float3();
        }
        Velocity.MergeFrom(other.Velocity);
      }
      if (other.rotation_ != null) {
        if (rotation_ == null) {
          rotation_ = new global::Innova.Common.Quaternion();
        }
        Rotation.MergeFrom(other.Rotation);
      }
      if (other.angular_ != null) {
        if (angular_ == null) {
          angular_ = new global::Innova.Common.Float4();
        }
        Angular.MergeFrom(other.Angular);
      }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public void MergeFrom(pb::CodedInputStream input) {
      uint tag;
      while ((tag = input.ReadTag()) != 0) {
        switch(tag) {
          default:
            input.SkipLastField();
            break;
          case 10: {
            if (location_ == null) {
              location_ = new global::Innova.Common.Float3();
            }
            input.ReadMessage(location_);
            break;
          }
          case 18: {
            if (velocity_ == null) {
              velocity_ = new global::Innova.Common.Float3();
            }
            input.ReadMessage(velocity_);
            break;
          }
          case 26: {
            if (rotation_ == null) {
              rotation_ = new global::Innova.Common.Quaternion();
            }
            input.ReadMessage(rotation_);
            break;
          }
          case 34: {
            if (angular_ == null) {
              angular_ = new global::Innova.Common.Float4();
            }
            input.ReadMessage(angular_);
            break;
          }
        }
      }
    }

  }

  public sealed partial class ActorScale : pb::IMessage<ActorScale> {
    private static readonly pb::MessageParser<ActorScale> _parser = new pb::MessageParser<ActorScale>(() => new ActorScale());
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public static pb::MessageParser<ActorScale> Parser { get { return _parser; } }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public static pbr::MessageDescriptor Descriptor {
      get { return global::Innova.Demo.InnovaDemoReflection.Descriptor.MessageTypes[2]; }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    pbr::MessageDescriptor pb::IMessage.Descriptor {
      get { return Descriptor; }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public ActorScale() {
      OnConstruction();
    }

    partial void OnConstruction();

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public ActorScale(ActorScale other) : this() {
      Scale = other.scale_ != null ? other.Scale.Clone() : null;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public ActorScale Clone() {
      return new ActorScale(this);
    }

    /// <summary>Field number for the "scale" field.</summary>
    public const int ScaleFieldNumber = 1;
    private global::Innova.Common.Float3 scale_;
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public global::Innova.Common.Float3 Scale {
      get { return scale_; }
      set {
        scale_ = value;
      }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public override bool Equals(object other) {
      return Equals(other as ActorScale);
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public bool Equals(ActorScale other) {
      if (ReferenceEquals(other, null)) {
        return false;
      }
      if (ReferenceEquals(other, this)) {
        return true;
      }
      if (!object.Equals(Scale, other.Scale)) return false;
      return true;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public override int GetHashCode() {
      int hash = 1;
      if (scale_ != null) hash ^= Scale.GetHashCode();
      return hash;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public override string ToString() {
      return pb::JsonFormatter.ToDiagnosticString(this);
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public void WriteTo(pb::CodedOutputStream output) {
      if (scale_ != null) {
        output.WriteRawTag(10);
        output.WriteMessage(Scale);
      }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public int CalculateSize() {
      int size = 0;
      if (scale_ != null) {
        size += 1 + pb::CodedOutputStream.ComputeMessageSize(Scale);
      }
      return size;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public void MergeFrom(ActorScale other) {
      if (other == null) {
        return;
      }
      if (other.scale_ != null) {
        if (scale_ == null) {
          scale_ = new global::Innova.Common.Float3();
        }
        Scale.MergeFrom(other.Scale);
      }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public void MergeFrom(pb::CodedInputStream input) {
      uint tag;
      while ((tag = input.ReadTag()) != 0) {
        switch(tag) {
          default:
            input.SkipLastField();
            break;
          case 10: {
            if (scale_ == null) {
              scale_ = new global::Innova.Common.Float3();
            }
            input.ReadMessage(scale_);
            break;
          }
        }
      }
    }

  }

  public sealed partial class ConfigGraphics : pb::IMessage<ConfigGraphics> {
    private static readonly pb::MessageParser<ConfigGraphics> _parser = new pb::MessageParser<ConfigGraphics>(() => new ConfigGraphics());
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public static pb::MessageParser<ConfigGraphics> Parser { get { return _parser; } }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public static pbr::MessageDescriptor Descriptor {
      get { return global::Innova.Demo.InnovaDemoReflection.Descriptor.MessageTypes[3]; }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    pbr::MessageDescriptor pb::IMessage.Descriptor {
      get { return Descriptor; }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public ConfigGraphics() {
      OnConstruction();
    }

    partial void OnConstruction();

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public ConfigGraphics(ConfigGraphics other) : this() {
      maxCameraDistance_ = other.maxCameraDistance_;
      maxViewDistance_ = other.maxViewDistance_;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public ConfigGraphics Clone() {
      return new ConfigGraphics(this);
    }

    /// <summary>Field number for the "max_camera_distance" field.</summary>
    public const int MaxCameraDistanceFieldNumber = 1;
    private float maxCameraDistance_;
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public float MaxCameraDistance {
      get { return maxCameraDistance_; }
      set {
        maxCameraDistance_ = value;
      }
    }

    /// <summary>Field number for the "max_view_distance" field.</summary>
    public const int MaxViewDistanceFieldNumber = 2;
    private float maxViewDistance_;
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public float MaxViewDistance {
      get { return maxViewDistance_; }
      set {
        maxViewDistance_ = value;
      }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public override bool Equals(object other) {
      return Equals(other as ConfigGraphics);
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public bool Equals(ConfigGraphics other) {
      if (ReferenceEquals(other, null)) {
        return false;
      }
      if (ReferenceEquals(other, this)) {
        return true;
      }
      if (MaxCameraDistance != other.MaxCameraDistance) return false;
      if (MaxViewDistance != other.MaxViewDistance) return false;
      return true;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public override int GetHashCode() {
      int hash = 1;
      if (MaxCameraDistance != 0F) hash ^= MaxCameraDistance.GetHashCode();
      if (MaxViewDistance != 0F) hash ^= MaxViewDistance.GetHashCode();
      return hash;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public override string ToString() {
      return pb::JsonFormatter.ToDiagnosticString(this);
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public void WriteTo(pb::CodedOutputStream output) {
      if (MaxCameraDistance != 0F) {
        output.WriteRawTag(13);
        output.WriteFloat(MaxCameraDistance);
      }
      if (MaxViewDistance != 0F) {
        output.WriteRawTag(21);
        output.WriteFloat(MaxViewDistance);
      }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public int CalculateSize() {
      int size = 0;
      if (MaxCameraDistance != 0F) {
        size += 1 + 4;
      }
      if (MaxViewDistance != 0F) {
        size += 1 + 4;
      }
      return size;
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public void MergeFrom(ConfigGraphics other) {
      if (other == null) {
        return;
      }
      if (other.MaxCameraDistance != 0F) {
        MaxCameraDistance = other.MaxCameraDistance;
      }
      if (other.MaxViewDistance != 0F) {
        MaxViewDistance = other.MaxViewDistance;
      }
    }

    [global::System.Diagnostics.DebuggerNonUserCodeAttribute]
    public void MergeFrom(pb::CodedInputStream input) {
      uint tag;
      while ((tag = input.ReadTag()) != 0) {
        switch(tag) {
          default:
            input.SkipLastField();
            break;
          case 13: {
            MaxCameraDistance = input.ReadFloat();
            break;
          }
          case 21: {
            MaxViewDistance = input.ReadFloat();
            break;
          }
        }
      }
    }

  }

  #endregion

}

#endregion Designer generated code