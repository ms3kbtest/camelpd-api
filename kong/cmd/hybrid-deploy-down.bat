@ECHO OFF
helm uninstall cp-release
helm uninstall dp-release
helm uninstall dp-release-2
kubectl delete -f ./kubernetes/camelpd-api-secrets.yaml