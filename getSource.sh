echo "rm RAppRTC begin"
rm -rf RAppRTC
echo "rm RAppRTC end"

echo "cp RAppRTC begin"
cp -r ~/AndroidStudioProjects/RAppRTC/ .
echo "cp RAppRTC end"

echo "sh updateSrc.sh"
sh updateSrc.sh

