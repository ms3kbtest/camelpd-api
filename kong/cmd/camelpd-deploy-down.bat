@ECHO OFF
kubectl delete -f kubernetes/camelpd-api-mysql.yaml
kubectl delete -f kubernetes/camelpd-api-deployment.yaml
kubectl delete -f kong/crd/camelpd-cors.yaml
kubectl delete -f kong/crd/camelpd-jwt.yaml
kubectl delete -f kong/crd/camelpd-name-restrict.yaml
kubectl delete -f kong/crd/camelpd-validate-request.yaml
kubectl delete -f kong/crd/camelpd-rate-limit.yaml
kubectl delete -f kong/crd/camelpd-svc-plugins.yaml
kubectl delete -f kong/crd/camelpd-ingress.yaml