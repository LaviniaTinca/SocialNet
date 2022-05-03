package team.proiectextins.service;


import team.proiectextins.domain.Friendship;
import team.proiectextins.domain.User;

import java.util.*;

public class Network {
    private final Long[][] matrix;
    private final int size;
    private final Set<Long> ids;

    public Network(int size) {
        this.ids = new HashSet<>();
        this.size = size;
        this.matrix = new Long[size][size];
        Arrays.stream(matrix).forEach(elem -> Arrays.fill(elem, 0L));
    }

    /**
     * adauga prietenia in network
     *
     * @param friendsList lista de prietenii
     */
    public void addFriendships(Iterable<Friendship> friendsList) {
        friendsList.forEach(f -> {
            int left = f.getId().getLeft().intValue();
            int right = f.getId().getRight().intValue();
            this.matrix[left - 1][right - 1] = 1L;
            this.matrix[right - 1][left - 1] = 1L;
        });
    }

    /**
     * adauga userii in network
     *
     * @param usersList lista de useri
     */
    public void addUsers(Iterable<User> usersList) {
        usersList.forEach(u -> ids.add(u.getId() - 1));
    }

    private void DFS(int v, boolean[] visited) {
        visited[v] = true;
        for (int i = 0; i < size; i++) {
            if (matrix[v][i] == 1L && !visited[i]) {
                DFS(i, visited);
            }
        }
    }

    /**
     * numara comunitatile
     *
     * @return nr de comunitati
     */
    public int connectedComponents() {
        int count = 0;
        boolean[] visited = new boolean[size];

        for (int i = 0; i < size; i++) {
            if (!visited[i] && ids.contains((long) i)) {
                count++;
                DFS(i, visited);
            }
        }

        return count;
    }

    /**
     * determina cea mai mare comunitate
     *
     * @return o lista cu id-urile comunitatii determinate
     */
    public List<Integer> biggestComponent() {
        List<Integer> list = new ArrayList<>();
        long maxConnections = 0;
        for (int i = 0; i < size; i++) {
            long count = Arrays.stream(matrix[i]).filter((elem) -> elem == 1).count();

            if (count > maxConnections) {
                maxConnections = count;
                list.clear();
                list.add(i + 1);

                for (int j = 0; j < size; j++) {
                    if (matrix[i][j] == 1) {
                        list.add(j + 1);
                    }
                }
            }
        }
        return list;
    }

}
