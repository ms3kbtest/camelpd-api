@ECHO OFF
kubectl create secret generic app-user-jwt --from-literal=kongCredType=jwt --from-literal=key="some-key" --from-literal=algorithm=HS256 --from-literal=secret="ultraWeakSecret"
kubectl apply -f kubernetes/camelpd-api-mysql.yaml
kubectl apply -f kubernetes/camelpd-api-deployment.yaml
kubectl apply -f kong/crd/camelpd-cors.yaml
kubectl apply -f kong/crd/camelpd-jwt.yaml
kubectl apply -f kong/crd/camelpd-name-restrict.yaml
kubectl apply -f kong/crd/camelpd-validate-request.yaml
kubectl apply -f kong/crd/camelpd-rate-limit.yaml
kubectl apply -f kong/crd/camelpd-svc-plugins.yaml
kubectl apply -f kong/crd/camelpd-ingress.yaml