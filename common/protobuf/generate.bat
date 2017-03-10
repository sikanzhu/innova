..\..\thirdparty\protoc-3.1.0-win32\bin\protoc.exe --proto_path=./ --java_out=../../server/common/src/ innova_common.proto
..\..\thirdparty\protoc-3.1.0-win32\bin\protoc.exe --proto_path=./ --csharp_out=../../demo/unity3d/InnovaDemo/Assets/innova/scripts/protobuf innova_common.proto

..\..\thirdparty\protoc-3.1.0-win32\bin\protoc.exe --proto_path=./ --java_out=../../server/innova/src/ innova_protocol.proto
..\..\thirdparty\protoc-3.1.0-win32\bin\protoc.exe --proto_path=./ --csharp_out=../../demo/unity3d/InnovaDemo/Assets/innova/scripts/protobuf innova_protocol.proto
pause