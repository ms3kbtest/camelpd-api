@ECHO OFF
helm uninstall kong-ee
kubectl delete -f ./kubernetes/camelpd-api-secrets.yaml